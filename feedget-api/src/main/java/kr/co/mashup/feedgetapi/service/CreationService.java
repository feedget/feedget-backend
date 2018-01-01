package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.CreationDto;
import kr.co.mashup.feedgetcommon.domain.Category;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.User;
import kr.co.mashup.feedgetcommon.repository.CategoryRepository;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by ethan.kim on 2017. 12. 23..
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CreationService {

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final CreationRepository creationRepository;

    @Transactional
    public long addCreation(long userId, CreationDto.Create dto) {
        User writer = userRepository.findOne(userId);
        if (writer == null) {
            throw new NotFoundException("not found writer");
        }

        if (writer.getCurrentPoint() < dto.getRewardPoint()) {
            throw new InvalidParameterException("exceed current point");
        }

        Optional<Category> categoryOp = categoryRepository.findByName(dto.getCategory());
        Category category = categoryOp.orElseThrow(() -> new NotFoundException("not found category"));

        Creation creation = new Creation();
        creation.setTitle(dto.getTitle());
        creation.setDescription(dto.getDescription());
        creation.setCategory(category);
        creation.setDueDate(LocalDateTime.now().plusDays(14));
        creation.setRewardPoint(dto.getRewardPoint());
        creation.setStatus(Creation.Status.PROCEEDING);
        creation.setWriter(writer);
        creation.setAnonymity(dto.isAnonymity());
        creation.setFeedbackCount(0L);
        creation = creationRepository.save(creation);

        writer.setCurrentPoint(writer.getCurrentPoint() - dto.getRewardPoint());
        writer.setPeriodPoint(writer.getPeriodPoint() - dto.getRewardPoint());
        userRepository.save(writer);

        return creation.getCreationId();
    }
}

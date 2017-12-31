package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.common.StorageProperties;
import kr.co.mashup.feedgetapi.common.util.CodeGenerator;
import kr.co.mashup.feedgetapi.common.util.FileUtil;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.CreationContent;
import kr.co.mashup.feedgetcommon.domain.code.ContentType;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 컨텐츠 관련 비즈니스 로직 처리
 * <p>
 * Created by ethankim on 2017. 11. 5..
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContentsService {

    private final CreationRepository creationRepository;

    private final StorageProperties storageProperties;

    @Transactional
    public void addContents(long creationId, ContentsDto dto) {
        ContentType contentType = ContentType.fromString(dto.getContentsType());
        List<MultipartFile> files = dto.getFiles();

        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        if (contentType == ContentType.IMAGE) {

            files.stream()
                    .filter(file -> !file.isEmpty())
                    .forEach(file -> {
                        // 파일 저장
                        String fileName = CodeGenerator.generateFileName(file.getOriginalFilename());
                        FileUtil.upload(file, FileUtil.getImageUploadPath(storageProperties.getPath(), creationId), fileName);

                        CreationContent content = new CreationContent();
                        content.setFileName(fileName);
                        content.setOriginalFileName(file.getOriginalFilename());
                        content.setSize(file.getSize());
                        content.setType(contentType);
                        content.setCreation(creation);
                        creation.addContent(content);
                    });

        } else if (contentType == ContentType.AUDIO) {
            // Todo: audio 저장 로직 추가
        }
    }
}

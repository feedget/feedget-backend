package kr.co.mashup.feedgetapi.service;

import kr.co.mashup.feedgetapi.common.StorageProperties;
import kr.co.mashup.feedgetapi.common.util.CodeGenerator;
import kr.co.mashup.feedgetapi.common.util.FileUtil;
import kr.co.mashup.feedgetapi.exception.InvalidParameterException;
import kr.co.mashup.feedgetapi.exception.NotFoundException;
import kr.co.mashup.feedgetapi.web.dto.ContentsDto;
import kr.co.mashup.feedgetapi.web.dto.FeedbackDto;
import kr.co.mashup.feedgetcommon.domain.Creation;
import kr.co.mashup.feedgetcommon.domain.CreationContent;
import kr.co.mashup.feedgetcommon.domain.Feedback;
import kr.co.mashup.feedgetcommon.domain.FeedbackAttachedContent;
import kr.co.mashup.feedgetcommon.domain.code.ContentType;
import kr.co.mashup.feedgetcommon.repository.CreationRepository;
import kr.co.mashup.feedgetcommon.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final FeedbackRepository feedbackRepository;

    private final StorageProperties storageProperties;

    /**
     * 창작물의 컨텐츠 추가
     *
     * @param creationId
     * @param dto
     */
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
                        content.setUrl(FileUtil.getImageUrl(storageProperties.getUri(), creation.getCreationId(), fileName));
                        creation.addContent(content);
                    });

        } else if (contentType == ContentType.AUDIO) {
            // Todo: audio 저장 로직 추가
        }
    }

    /**
     * 창작물의 컨텐츠 제거
     *
     * @param creationId
     * @param contentIds
     */
    @Transactional
    public void removeContents(long creationId, List<Long> contentIds) {
        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        // Todo: fetch join으로 수정?
        List<CreationContent> contents = creation.getContents();
        CopyOnWriteArrayList<CreationContent> removeContents = new CopyOnWriteArrayList<>(contents);
        removeContents.stream()
                .filter(content -> contentIds.contains(content.getCreationContentId()))
                .forEach(content -> {
                    // 파일 제거
                    String filePath = FileUtil.getImageUploadPath(storageProperties.getPath(), creationId) + "/" + content.getFileName();
                    FileUtil.deleteFile(filePath);

                    creation.removeContent(content);
                });
    }

    /**
     * 피드백의 첨부 컨텐츠 추가
     *
     * @param creationId
     * @param feedbackId
     * @param dto
     */
    @Transactional
    /*
    Todo: refactoring
        content -> feedback -> image
                            -> audio
                -> creation -> image
                            -> audio
                      enum에 대한 컨텐츠를 저장할 수 있는 서비스를 가진 어떤 것을 만들고,
                      그 enum이면 그 서비스의 저장 로직을 태운다?
         */
    public void addFeedbackAttachedContents(long creationId, long feedbackId, FeedbackDto.AttachedContent dto) {
        ContentType contentType = ContentType.fromString(dto.getContentsType());
        List<MultipartFile> files = dto.getFiles();

        Optional<Feedback> feedbackOp = feedbackRepository.findByFeedbackId(feedbackId);
        Feedback feedback = feedbackOp.orElseThrow(() -> new NotFoundException("not found feedback"));

        if (!feedback.fromCreation(creationId)) {
            // Todo: exception 수정
            throw new InvalidParameterException("forbidden request");
        }

        if (contentType == ContentType.IMAGE) {
            files.stream()
                    .filter(file -> !file.isEmpty())
                    .forEach(file -> {
                        // 파일 저장
                        // Todo: storage path, url 로직 이동. storage/creations/3/feedack/img_name.jpg
                        String fileName = CodeGenerator.generateFileName(file.getOriginalFilename());
                        FileUtil.upload(file, String.format("%s/creations/%d/feedback/%d", storageProperties.getPath(), creationId, feedbackId), fileName);

                        FeedbackAttachedContent content = new FeedbackAttachedContent();
                        content.setFileName(fileName);
                        content.setOriginalFileName(file.getOriginalFilename());
                        content.setSize(file.getSize());
                        content.setType(contentType);
                        content.setFeedback(feedback);
                        content.setUrl(String.format("%s/creations/%d/feedback/%d/%s", storageProperties.getUri(), creationId, feedbackId, fileName));
                        feedback.addAttachedContent(content);
                    });

        } else if (contentType == ContentType.AUDIO) {
            // Todo: audio 저장 로직 추가
        }
    }

    /**
     * 피드백의 첨부 컨텐츠 제거
     *
     * @param creationId
     * @param contentIds
     */
    @Transactional
    public void removeFeedbackAttachedContents(long creationId, long feedbackId, List<Long> contentIds) {
        Optional<Feedback> feedbackOp = feedbackRepository.findByFeedbackId(feedbackId);
        Feedback feedback = feedbackOp.orElseThrow(() -> new NotFoundException("not found feedback"));

        if (!feedback.fromCreation(creationId)) {
            throw new InvalidParameterException("forbidden request");
        }

        // Todo: fetch join으로 수정?
        List<FeedbackAttachedContent> contents = feedback.getAttachedContents();
        CopyOnWriteArrayList<FeedbackAttachedContent> removeContents = new CopyOnWriteArrayList<>(contents);
        removeContents.stream()
                .filter(content -> contentIds.contains(content.getFeedbackAttachedContentId()))
                .forEach(content -> {
                    // 파일 제거
                    String filePath = String.format("%s/creations/%d/feedback/%d/%s", storageProperties.getPath(), creationId, feedbackId, content.getFileName());
                    FileUtil.deleteFile(filePath);

                    feedback.removeAttachedContent(content);
                });
    }
}

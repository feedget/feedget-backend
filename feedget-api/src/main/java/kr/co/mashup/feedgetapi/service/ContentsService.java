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
}

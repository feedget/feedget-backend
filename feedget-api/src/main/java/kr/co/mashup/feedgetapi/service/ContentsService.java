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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
     * 창작물의 컨텐츠 수정
     *
     * @param creationId
     * @param dto
     */
    @Transactional
    public void modifyContents(long creationId, ContentsDto dto) {
        ContentType contentType = ContentType.fromString(dto.getContentsType());
        List<MultipartFile> files = dto.getFiles();

        // Todo: fetch join으로 수정?
        Optional<Creation> creationOp = creationRepository.findByCreationId(creationId);
        Creation creation = creationOp.orElseThrow(() -> new NotFoundException("not found creation"));

        List<CreationContent> contents = creation.getContents();

        // 원본 파일 이름이 같다면 같은 파일로 취급
        // Todo: 같은 파일인지 체크 로직 개선 - 복합적인 조건으로 체크? ex. hash 값 비교 등..
        if (contentType == ContentType.IMAGE) {

            // 제거
            Map<String, MultipartFile> fileMap = files.stream()
                    .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file));

            CopyOnWriteArrayList<CreationContent> copyOnWriteContents = new CopyOnWriteArrayList<>(contents);
            copyOnWriteContents.stream()
                    .filter(content -> !fileMap.containsKey(content.getOriginalFileName()))
                    .forEach(content -> {
                        // 파일 제거
                        String filePath = FileUtil.getImageUploadPath(storageProperties.getPath(), creationId) + "/" + content.getFileName();
                        FileUtil.deleteFile(filePath);

                        creation.removeContent(content);
                    });

            // 추가
            Map<String, CreationContent> contentMap = contents.stream()
                    .collect(Collectors.toMap(CreationContent::getOriginalFileName, content -> content));
            files.stream()
                    .filter(file -> !file.isEmpty() && !contentMap.containsKey(file.getOriginalFilename()))
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

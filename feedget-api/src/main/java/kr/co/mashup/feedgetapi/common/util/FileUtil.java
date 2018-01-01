package kr.co.mashup.feedgetapi.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ethankim on 2017. 11. 4..
 */
public class FileUtil {

    public static File upload(MultipartFile multipartFile, String uploadDir, String fileName) {
        File dirPath = new File(uploadDir);
        if (!dirPath.exists()) {
            boolean made = dirPath.mkdirs();  // 해당 경로에 없는 dir 모두 생성
            if (!made) {
                throw new RuntimeException(String.format("make directory(%s) fail", uploadDir));
            }
        }

        String targetFilePath = uploadDir + "/" + fileName;
        File targetFile = new File(targetFilePath);

        try {
            // file save 방법1
            multipartFile.transferTo(targetFile);

            // file save 방법2
//            byte[] bytes = multipartFile.getBytes();
//            BufferedOutputStream bufferStream =
//                    new BufferedOutputStream(new FileOutputStream(targetFile));
//            bufferStream.write(bytes);
//            bufferStream.close();

        } catch (IOException e) {
            throw new RuntimeException(String.format("file upload error: %s", targetFile), e);
        }
        return targetFile;
    }

    public static boolean moveFile(String source, String dest) {
        Path file = Paths.get(source);
        Path movePath = Paths.get(dest);

        boolean movoPathExists = Files.exists(movePath,
                LinkOption.NOFOLLOW_LINKS);

        if (!movoPathExists) {  // 디렉토리 없으면 생성
            try {
                Files.createDirectories(movePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.move(file, movePath.resolve(file.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean deleteFile(String imageFilePath) {
        File imageFile = new File(imageFilePath);
        if (!imageFile.exists()) {
            return false;
        }
        return imageFile.delete();
    }

    public static boolean deleteFilesInDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return false;
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
        return true;
    }

    public static boolean deleteDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return false;
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir(file.getPath());
            }
        }
        return dir.delete();
    }

    /**
     * url에서 file name 추출
     *
     * @param imageUrl image url
     * @return file name
     */
    public static String getFileName(String imageUrl) {
        // uri/tmp/fileName
        //Todo: confirm url pattern matching??
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    /**
     * 임시 저장할 물리적인 image upload path 제공
     *
     * @return 임시 저장할 image upload path
     */
    public static String getImageUploadTempPath(String storagePath) {
        return String.format("%s/tmp", storagePath);
    }

    /**
     * 물리적인 image upload path 제공
     *
     * @return upload path
     */
    public static String getImageUploadPath(String storagePath, Long id) {
        return String.format("%s/%d", storagePath, id);
    }

    /**
     * 임시저장된 이미지를 다운받을 수 있는 url 제공
     *
     * @return temporarily image url
     */
    public static String getTempImageUrl(String storageUri, String fileName) {
        return String.format("%s/tmp/%s", storageUri, fileName);
    }

    /**
     * 업로드된 이미지를 다운받을 수 있는 url 제공
     *
     * @return image url
     */
    public static String getImageUrl(String storageUri, Long id, String fileName) {
        return String.format("%s/%d/%s", storageUri, id, fileName);
    }
}

package com.bizmate.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUtil {

    // application.properties에 설정한 파일 저장 경로를 가져옴
    @Value("${com.bizmate.upload.path}")
    private String uploadPath;

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 원본 파일 이름 가져오기
        String originalFilename = file.getOriginalFilename();

        // UUID를 이용해 고유한 파일 이름 생성
        String uuid = UUID.randomUUID().toString();
        String savedFilename = uuid + "_" + originalFilename;

        // 저장할 경로와 파일 이름을 합쳐 Path 객체 생성
        Path savePath = Paths.get(uploadPath, savedFilename);

        // 해당 경로에 파일 저장
        file.transferTo(savePath);

        return savedFilename;
    }
}
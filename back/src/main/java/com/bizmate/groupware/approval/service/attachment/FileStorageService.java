package com.bizmate.groupware.approval.service.attachment;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.hr.dto.user.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document, UserDTO uploader);

    void deleteFile(String filePath);
}

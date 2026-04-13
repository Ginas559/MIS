package vn.iotstar.coolenglish.util;

import java.io.IOException;

import jakarta.servlet.http.Part;
import vn.iotstar.coolenglish.service.IImageUploadService;
import vn.iotstar.coolenglish.service.impl.ImageUploadServiceFactory;

public final class UploadUtils {

    private UploadUtils() {
    }

    public static String uploadAvatar(Part filePart, Long userId) throws IOException {
        validateFilePart(filePart, "File avatar khong duoc de trong");
        String publicId = userId == null ? null : "user_" + userId;
        return uploadWithOptionalPublicId(filePart, publicId, "Loi upload avatar: ");
    }

    public static String uploadAcademicContentImage(Part filePart, Long contentId) throws IOException {
        validateFilePart(filePart, "File anh khong duoc de trong");
        String publicId = contentId == null ? null : "content_" + contentId;
        return uploadWithOptionalPublicId(filePart, publicId, "Loi upload anh hoc lieu: ");
    }

    public static String uploadImage(Part filePart) throws IOException {
        validateFilePart(filePart, "File anh khong duoc de trong");
        try {
            IImageUploadService uploadService = ImageUploadServiceFactory.createDefaultService();
            return uploadService.uploadImage(filePart);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Loi upload anh: " + e.getMessage(), e);
        }
    }

    public static boolean deleteImage(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return false;
        }
        try {
            IImageUploadService uploadService = ImageUploadServiceFactory.createDefaultService();
            return uploadService.deleteImage(publicId);
        } catch (Exception e) {
            return false;
        }
    }

    private static String uploadWithOptionalPublicId(Part filePart, String publicId, String errorPrefix)
            throws IOException {
        try {
            IImageUploadService uploadService = ImageUploadServiceFactory.createDefaultService();
            if (publicId == null || publicId.isBlank()) {
                return uploadService.uploadImage(filePart);
            }
            return uploadService.uploadImage(filePart, publicId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(errorPrefix + e.getMessage(), e);
        }
    }

    private static void validateFilePart(Part filePart, String message) {
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}


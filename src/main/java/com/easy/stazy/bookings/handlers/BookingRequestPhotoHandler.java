package com.easy.stazy.bookings.handlers;

import com.easy.stazy.pgmanagement.tenant.dto.request.TenantProofDto;
import com.easy.stazy.bookings.entity.BookingRequestEntity;
import com.easy.stazy.bookings.entity.BookingRequestPhotoProofEntity;
import com.easy.stazy.photos.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class BookingRequestPhotoHandler {
    private final FileStorageService fileStorageService;

    @Value("${booking.photos.id-proof-dir}")
    private String bookingProofPhotoUploadDir;

    @Value("${booking.photos.profile-dir}")
    private String bookingProfilePhotoUploadDir;


    public BookingRequestPhotoHandler(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void uploadProofPhotos(BookingRequestEntity booking, List<TenantProofDto> proofDtos) {
        if (proofDtos == null || proofDtos.isEmpty()) return;
        deleteOldProofPhotos(booking);
        List<BookingRequestPhotoProofEntity> newProofPhotos = uploadNewProofPhotos(booking, proofDtos);
        if (booking.getProofPhotos() == null) {
            booking.setProofPhotos(new ArrayList<>());
        }
        booking.getProofPhotos().addAll(newProofPhotos);
        // Ensure bidirectional link for all proof photos (defensive)
        for (BookingRequestPhotoProofEntity proof : booking.getProofPhotos()) {
            proof.setBookingRequest(booking);
        }
    }

    private void deleteOldProofPhotos(BookingRequestEntity booking) {
        if (booking.getProofPhotos() != null) {
            Iterator<BookingRequestPhotoProofEntity> iterator = booking.getProofPhotos().iterator();
            while (iterator.hasNext()) {
                BookingRequestPhotoProofEntity oldProof = iterator.next();
                if (oldProof.getProofPhotoUrl() != null) {
                    fileStorageService.deleteFile(oldProof.getProofPhotoUrl());
                }
                iterator.remove();
            }
        }
    }

    private List<BookingRequestPhotoProofEntity> uploadNewProofPhotos(BookingRequestEntity booking, List<TenantProofDto> proofDtos) {
        List<BookingRequestPhotoProofEntity> newProofPhotos = new ArrayList<>();
        for (TenantProofDto proofDto : proofDtos) {
            MultipartFile proofPhotoFile = proofDto.getFile();
            String uploadedUrl = null;
            if (proofPhotoFile != null && !proofPhotoFile.isEmpty()) {
                // Save in subfolder with booking ID
                String uploadDir = bookingProofPhotoUploadDir + java.io.File.separator + booking.getId() + java.io.File.separator + "id-proof-photos";
                List<String> urls = fileStorageService.uploadFile(List.of(proofPhotoFile), uploadDir);
                if (!urls.isEmpty()) {
                    uploadedUrl = urls.get(0);
                }
            }
            if (uploadedUrl != null) {
                BookingRequestPhotoProofEntity entity = new BookingRequestPhotoProofEntity();
                entity.setBookingRequest(booking); // Ensure bidirectional link is set
                entity.setProofType(proofDto.getProofType());
                entity.setProofPhotoUrl(uploadedUrl);
                newProofPhotos.add(entity);
            }
        }
        return newProofPhotos;
    }

    public String handleProfilePhoto(BookingRequestEntity requestEntity, MultipartFile profilePhoto) {
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            if (requestEntity.getProofPhotos() == null) {
                requestEntity.setProofPhotos(new ArrayList<>());
            }
            if (requestEntity.getProofPhotos() != null && requestEntity.getTenantPhotoUrl() != null && !requestEntity.getTenantPhotoUrl().isEmpty()) {
                fileStorageService.deleteFile(requestEntity.getTenantPhotoUrl());
            }
            // Save in subfolder with booking ID
            String uploadDir = bookingProfilePhotoUploadDir + java.io.File.separator + requestEntity.getId() + java.io.File.separator + "profile-photos";
            List<String> profilePhotoPaths = fileStorageService.uploadFile(List.of(profilePhoto), uploadDir);
            if (!profilePhotoPaths.isEmpty()) {
                requestEntity.setTenantPhotoUrl(profilePhotoPaths.get(0));
            }
        }
        return requestEntity.getTenantPhotoUrl();
    }

    /**
     * Moves a proof photo from booking-request-photos to the tenant folder and returns the new URL.
     */
    public String moveProofPhotoToTenantFolder(String oldUrl, Long tenantId, String tenantProofPhotoUploadDir) {
        return fileStorageService.moveFileToTenantFolder(oldUrl, tenantId, tenantProofPhotoUploadDir, "id-proof-photos");
    }

    /**
     * Moves a profile photo from booking-request-photos to the tenant folder and returns the new URL.
     */
    public String moveProfilePhotoToTenantFolder(String oldUrl, Long tenantId, String tenantProfilePhotoUploadDir) {
        return fileStorageService.moveFileToTenantFolder(oldUrl, tenantId, tenantProfilePhotoUploadDir, "profile-photos");
    }
}

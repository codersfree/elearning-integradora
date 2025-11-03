package com.example.codersfree.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.codersfree.dto.CoverDto;
import com.example.codersfree.model.Cover;
import com.example.codersfree.repository.CoverRepository;
import com.example.codersfree.web.util.PageWrapper;

@Service
public class CoverService {

    @Autowired
    private CoverRepository coverRepository;

    @Autowired
    private FileStorageService storage;
 
    @Transactional(readOnly = true)
    public Cover findById(Long id) {
        return coverRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cover not found"));
    }

    @Transactional(readOnly = true)
    public List<Cover> findAll() {
        return coverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageWrapper<Cover> findPaginate(Pageable pageable) {
        Page<Cover> covers = coverRepository.findAll(pageable);
        return new PageWrapper<>(covers);
    }

    @Transactional
    public Cover save(CoverDto coverDto, MultipartFile file)  throws IOException 
    {
        Cover cover = Cover.builder()
            .title(coverDto.getTitle())
            .startAt(coverDto.getStartAt())
            .endAt(coverDto.getEndAt())
            .active(coverDto.isActive())
            .build();

        if (file != null && !file.isEmpty()) {
            String imagePath = storage.save("covers/", file);
            cover.setImagePath(imagePath);
        }

        return coverRepository.save(cover);
    }

    public Cover update(Long id, CoverDto coverDto, MultipartFile file) throws IOException {
        Cover cover = findById(id);

        cover.setTitle(coverDto.getTitle());
        cover.setStartAt(coverDto.getStartAt());
        cover.setEndAt(coverDto.getEndAt());
        cover.setActive(coverDto.isActive());

        if (file != null && !file.isEmpty()) {
            // Delete old image if exists
            if (cover.getImagePath() != null && !cover.getImagePath().isEmpty()) {
                storage.delete(cover.getImagePath());
            }
            String imagePath = storage.save("covers/", file);
            cover.setImagePath(imagePath);
        }

        return coverRepository.save(cover);
    }

    public void delete(Long id) throws IOException {
        Cover cover = findById(id);

        if (cover.getImagePath() != null && !cover.getImagePath().isEmpty()) {
            storage.delete(cover.getImagePath());
        }

        coverRepository.delete(cover);
    }
    
}

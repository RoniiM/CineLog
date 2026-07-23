package com.cinelog.controller;

import com.cinelog.dto.DiaryEntryDto;
import com.cinelog.dto.DiaryEntryRequest;
import com.cinelog.service.DiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping
    public ResponseEntity<DiaryEntryDto> addEntry(@PathVariable Long userId, @Valid @RequestBody DiaryEntryRequest request) {
        DiaryEntryDto dto = diaryService.addDiaryEntry(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public List<DiaryEntryDto> getDiary(@PathVariable Long userId) {
        return diaryService.getUserDiary(userId);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long userId, @PathVariable Long entryId) {
        diaryService.deleteDiaryEntry(userId, entryId);
        return ResponseEntity.noContent().build();
    }
}

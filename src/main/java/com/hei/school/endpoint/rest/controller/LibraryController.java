package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.LibraryDTO;
import com.hei.school.entity.Library;
import com.hei.school.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/libraries")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping
    public List<Library> getAllLibraries() {
        return libraryService.getAll();
    }

    @GetMapping("/{id}")
    public Library getLibraryById(@PathVariable UUID id) {
        return libraryService.getById(id);
    }

    @GetMapping("/search")
    public List<Library> searchLibraries(@RequestParam String name) {
        return libraryService.searchByName(name);
    }

    @PostMapping
    public Library createLibrary(@RequestBody LibraryDTO dto) {
        return libraryService.create(dto);
    }

    @PutMapping("/{id}")
    public Library updateLibrary(@PathVariable UUID id, @RequestBody LibraryDTO dto) {
        return libraryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteLibrary(@PathVariable UUID id) {
        libraryService.delete(id);
    }
}
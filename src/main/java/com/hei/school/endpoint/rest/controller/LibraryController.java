package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.LibraryDTO;
import com.hei.school.entity.Library;
import com.hei.school.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librairies")
public class LibraryController {
    @Autowired
    private LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<Library>> getAll(){
        return ResponseEntity.ok(libraryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Library> getById(@PathVariable Long id){
        return ResponseEntity.ok(libraryService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Library>> searchByName(@RequestParam String name){
        return ResponseEntity.ok(libraryService.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<Library> create(@RequestBody LibraryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Library> update(@PathVariable Long id, @RequestBody LibraryDTO dto) {
        return ResponseEntity.ok(libraryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        libraryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

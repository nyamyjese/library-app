package com.hei.school.service;

import com.hei.school.dto.LibraryDTO;
import com.hei.school.entity.Library;
import com.hei.school.repository.LibraryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {

  @Autowired private LibraryRepository libraryRepository;

  public List<Library> getAll() {
    return libraryRepository.findAll();
  }

  public Library getById(UUID id) {
    return libraryRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Library not found : " + id));
  }

  public List<Library> searchByName(String name) {
    return libraryRepository.findByNameContainingIgnoreCase(name);
  }

  public Library create(LibraryDTO dto) {
    Library library = new Library();
    library.setName(dto.getName());
    library.setAddress(dto.getAddress());
    library.setPhone(dto.getPhone());
    return libraryRepository.save(library);
  }

  public Library update(UUID id, LibraryDTO dto) {
    Library library = getById(id);
    library.setName(dto.getName());
    library.setAddress(dto.getAddress());
    library.setPhone(dto.getPhone());
    return libraryRepository.save(library);
  }

  public void delete(UUID id) {
    libraryRepository.deleteById(id);
  }
}

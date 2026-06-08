package com.hei.school.repository;

import org.apache.tomcat.jni.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    List<Library> findByName(String name);
}

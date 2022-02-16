package com.example;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.entity.Request;

public interface RequestRepository extends PagingAndSortingRepository<Request, Long> {

}

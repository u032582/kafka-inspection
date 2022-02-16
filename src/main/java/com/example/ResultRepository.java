package com.example;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.entity.Result;

public interface ResultRepository extends PagingAndSortingRepository<Result, Long> {

}

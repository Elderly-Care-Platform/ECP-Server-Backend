package com.beautifulyears.repository;

import java.util.List;

import com.beautifulyears.domain.JustDailServices;
import com.beautifulyears.repository.custom.JustDialServiceRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JustDialSerivcesRepository extends PagingAndSortingRepository<JustDailServices, String>,JustDialServiceRepositoryCustom {
    public List<JustDailServices> findAll();

    public JustDailServices findById(String id);
}

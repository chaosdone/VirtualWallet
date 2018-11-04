package com.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.model.PrimaryTransaction;

public interface PrimaryTransactionDao extends CrudRepository<PrimaryTransaction, Long> {

	List<PrimaryTransaction> findAll();
}

package me.nixuehan.tcc.repository;

import me.nixuehan.tcc.transaction.ResourceEntity;

import java.util.List;

public interface TransactionRepository {

    boolean create(ResourceEntity resource);

    boolean delete(ResourceEntity resource);

    boolean delete();

    boolean update(ResourceEntity resource);

    List<ResourceEntity> find(String getGlobalTransactionId);

    List<ResourceEntity> findUnmodified();
}

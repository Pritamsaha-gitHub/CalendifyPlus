package com.assignment.CalendifyPlus.Repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositoryHandler {

    private MongoTemplate mongoTemplate;

    public RepositoryHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public  <T>T save (T object) {
        return mongoTemplate.save(object);
    }

    public  <T> List<T> findall (Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    public <T> List<T> findByField(String fieldName, Object value, Class<T> entityType) {
        Query query = new Query(Criteria.where(fieldName).is(value));
        return mongoTemplate.find(query, entityType);
    }

    public <T> List<T> findDocumentsByField(String fieldName, List<String> value, Class<T> entityType){
        Query query = new Query(Criteria.where(fieldName).in(value));
        return mongoTemplate.find(query, entityType);
    }
}


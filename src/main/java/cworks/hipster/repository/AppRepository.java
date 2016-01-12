package cworks.hipster.repository;

import cworks.hipster.domain.App;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the App entity.
 */
public interface AppRepository extends MongoRepository<App,String> {

}

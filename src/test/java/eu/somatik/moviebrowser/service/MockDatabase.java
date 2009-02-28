package eu.somatik.moviebrowser.service;

import java.util.HashMap;

import eu.somatik.moviebrowser.database.InMemoryDatabase;
import eu.somatik.moviebrowser.domain.StorableMovie;

public class MockDatabase extends InMemoryDatabase {

    InMemoryDatabase.IdGenerator generator;
    
    public MockDatabase() {
        generator = new IdGenerator();
    }

    @Override
    protected void checkId(StorableMovie movie) {
        generator.checkId(movie);
    }

    @Override
    protected void load() {
        this.movies = new HashMap<Long, StorableMovie>();

    }

    @Override
    protected void save() {
        // TODO Auto-generated method stub

    }

}

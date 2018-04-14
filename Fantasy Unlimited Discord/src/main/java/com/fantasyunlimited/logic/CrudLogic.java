package com.fantasyunlimited.logic;

import java.util.List;

public interface CrudLogic<T> {
	public T save(T entity);
	public void delete(T entity);
	public T getById(Integer id);
	public List<T> getAll();
}

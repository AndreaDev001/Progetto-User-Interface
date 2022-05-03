package com.progetto.progetto.model.sql;

import javafx.concurrent.Task;

import java.sql.SQLException;

public interface ITaskResult<T> {

    void onSuccess(T result);
    void onFail(Throwable exception);

    T getTaskMethod(Task<T> task) throws Exception;

}

package com.mlops.api.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.mlops.api.resources.DiscoveryResource;
import com.mlops.api.resources.WorkspaceResource;
import com.mlops.api.resources.ModelResource;
import com.mlops.api.exceptions.WorkspaceNotEmptyExceptionMapper;
import com.mlops.api.exceptions.LinkedWorkspaceNotFoundExceptionMapper;
import com.mlops.api.exceptions.ModelDeprecatedExceptionMapper;
import com.mlops.api.exceptions.GlobalExceptionMapper;
import com.mlops.api.filters.ApiLoggingFilter;

@ApplicationPath("/api/v1")
public class MLOpsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(DiscoveryResource.class);
        classes.add(WorkspaceResource.class);
        classes.add(ModelResource.class);

        classes.add(WorkspaceNotEmptyExceptionMapper.class);
        classes.add(LinkedWorkspaceNotFoundExceptionMapper.class);
        classes.add(ModelDeprecatedExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        classes.add(ApiLoggingFilter.class);

        return classes;
    }
}

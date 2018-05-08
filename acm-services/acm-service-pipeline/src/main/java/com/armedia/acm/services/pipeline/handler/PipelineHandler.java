package com.armedia.acm.services.pipeline.handler;

/*-
 * #%L
 * ACM Service: Pipeline
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

/**
 * Interface that all handlers for particular entity type must implement.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 26.07.2015.
 */
public interface PipelineHandler<T, S extends AbstractPipelineContext>
{
    /**
     * Execute handler actions.
     *
     * @param entity
     *            currently processed entity
     * @param pipelineContext
     *            pipeline context
     * @throws PipelineProcessException
     *             on error
     */
    void execute(T entity, S pipelineContext) throws PipelineProcessException;

    /**
     * In case of error, try to revert all the changes applied with execute() method.
     *
     * @param entity
     *            currently processed entity
     * @param pipelineContext
     *            pipeline context
     * @throws PipelineProcessException
     *             on error
     */
    void rollback(T entity, S pipelineContext) throws PipelineProcessException;
}

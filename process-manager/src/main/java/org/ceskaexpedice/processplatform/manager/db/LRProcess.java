/*
 * Copyright (C) 2012 Pavel Stastny
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ceskaexpedice.processplatform.manager.db;

import org.ceskaexpedice.processplatform.common.entity.ProcessState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Properties;

/**
 * Represents one running process
 *
 * @author pavels
 */
public interface LRProcess {

    /**
     * Parameters to process
     *
     * @return parameters
     */
    public List<String> getParameters();

    /**
     * Runtime parameters of this process
     *
     * @param params new params
     */
    public void setParameters(List<String> params);


    /**
     * Return unique identifier of LRPprocess
     *
     * @return UUID of process
     */
    public String getUUID();

    /**
     * Return process pid
     *
     * @return System PID of process
     */
    public String getPid();

    /**
     * Method for setting proceses's pid
     *
     * @param pid new system PID
     */
    public void setPid(String pid);

    /**
     * Process definintion id
     *
     * @return returns definition identification
     * @see LRProcessDefinition#getId()
     */
    public String getDefinitionId();

    //TODO: Vyhodit

    /**
     * Returns process description
     */
    @Deprecated
    public String getDescription();

    /**
     * Plan process to start
     *
     * @param paramsMapping Parameters mapping
     * @return process_id of registered process, possibly null in some error sitautions
     */
    public Integer planMe(Properties paramsMapping, String ipAddress);


    public void startMe(boolean wait, String krameriusAppLib, String... additionalJarFiles);

    /**
     * Stops underlaying os process
     */
    public void stopMe();

    /**
     * Returns timestamp start of process
     *
     * @return returns start timestamp
     */
    public long getStartTime();


    /**
     * Set time of the start of the process
     *
     * @param start new start timestamp
     */
    public void setStartTime(long start);

    /**
     * Return planned timestamp
     *
     * @return planned timestamp
     */
    public long getPlannedTime();

    /**
     * Sets planned timestamp
     *
     * @param ptime planned timestamp
     */
    public void setPlannedTime(long ptime);

    /**
     * Returns finished timestamp
     *
     * @return finished timestamp
     */
    public long getFinishedTime();


    /**
     * Sets finished timestamp
     *
     * @param new finished timestamp
     */
    public void setFinishedTime(long finishedtime);


    //TODO: Vyhodit

    /**
     * Returns true if underlaying process can be stopped
     */
    @Deprecated
    public boolean canBeStopped();

    /**
     * Returns current processes state
     *
     * @return current process state
     * @see States
     */
    public ProcessState getProcessState();


    /**
     * Setting process's state
     *
     * @param st new process state
     */
    public void setProcessState(ProcessState st);


    /**
     * Returns true, if the process is alive
     *
     * @return true if process is alive
     */
    public boolean isLiveProcess();

    /**
     * Returns process name
     *
     * @return name of process
     */
    public String getProcessName();

    /**
     * Sets process name
     *
     * @param nm new process name
     */
    public void setProcessName(String nm);

    /**
     * Returns stdout as stream
     *
     * @return Standard output stream
     * @throws FileNotFoundException OutputStream file doesn't exist
     */
    //FIXME: prejmenovat metodu, je to InputStream
    public InputStream getStandardProcessOutputStream() throws FileNotFoundException;

    /**
     * Returns errout as stream
     *
     * @return err output stream
     * @throws FileNotFoundException ErrStream file doesn't exist
     */
    //FIXME: prejmenovat metodu, je to InputStream
    public InputStream getErrorProcessOutputStream() throws FileNotFoundException;

    /**
     * Returns stdout as RandomAccessFile
     *
     * @return stdout RandomAccessFile
     * @throws FileNotFoundException Stdout file doens't exist
     */
    public RandomAccessFile getStandardProcessRAFile() throws FileNotFoundException;

    /**
     * Returns errout as RandomAccessFile
     *
     * @return Err file as RandomAccessFile
     * @throws FileNotFoundException Err file doesn't exist
     */
    public RandomAccessFile getErrorProcessRAFile() throws FileNotFoundException;


    public File getProcessErrorFile();

    public File getProcessOutputFile();


    /**
     * Returns process working directory (property 'user.dir')
     *
     * @return process working directory
     */
    public File processWorkingDirectory();


    /**
     * Returns token associated with this process
     *
     * @return Grouping token
     */
    public String getGroupToken();

    /**
     * Associate grouping token with this process
     *
     * @param token Grouping token
     */
    public void setGroupToken(String token);

    /**
     * Returns authentication token
     *
     * @return Authentication token
     */
    //TODO: Auth token is not necessary now
    public String getAuthToken();

    /**
     * Sets new authentication token
     *
     * @param authToken authentication token
     */
    //TODO: Auth token is not necessary now
    public void setAuthToken(String authToken);


    /**
     * Returns login name of the user (who has started this process)
     *
     * @return login name
     */
    public String getLoginname();

    /**
     * Sets login name
     *
     * @return login name
     */
    public void setLoginname(String lname);

    /**
     * Returns surname of the user (who has started this process)
     *
     * @return surname
     */
    public String getSurname();

    /**
     * Sets surname
     *
     * @param sname new surname
     */
    public void setSurname(String sname);

    /**
     * Returns firstname of the user (who has started this process)
     *
     * @return firstname
     */
    public String getFirstname();

    /**
     * Sets firstname
     *
     * @param fname firstname
     */
    public void setFirstname(String fname);

    /**
     * Returns logged user key
     *
     * @return logged user key
     */
    @Deprecated
    public String getLoggedUserKey();

    /**
     * Sets logged user key
     *
     * @param loggedUserKey sets loggeduserkey
     */
    public void setLoggedUserKey(String loggedUserKey);

    /**
     * Returns true, if this process is mater process
     *
     * @return true if this process is master process
     */
    public boolean isMasterProcess();

    /**
     * Sets flag for master process
     *
     * @param flag master process flag
     */
    public void setMasterProcess(boolean flag);

    /**
     * Returns parameters mapping
     *
     * @return Parameters mapping
     */
    public Properties getParametersMapping();

    /**
     * Sets the parameters mapping
     *
     * @param parametersMapping new parameters mapping
     */
    public void setParametersMapping(Properties parametersMapping);

    /**
     * Returns IP address associated with HTTP request
     *
     * @return
     */
    public String getPlannedIPAddress();

    /**
     * Sets IP address
     *
     * @param ipAddr
     */
    public void setPlannedIPAddress(String ipAddr);


    /**
     * Returns owner's id
     *
     * @return
     */
    public String getOwnerId();

    /**
     * Sets owner's id
     *
     * @param ownerId
     */
    public void setOwnerId(String ownerId);

    /**
     * Returns owner's whole name
     *
     * @return
     */
    public String getOwnerName();

    /**
     * Sets owner's name
     *
     * @param ownerName
     * @return
     */
    public void setOwnerName(String ownerName);

}

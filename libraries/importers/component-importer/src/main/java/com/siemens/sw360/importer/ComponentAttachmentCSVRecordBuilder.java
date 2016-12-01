/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.importer;

import org.eclipse.sw360.datahandler.thrift.attachments.Attachment;
import org.eclipse.sw360.datahandler.thrift.components.Component;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.apache.commons.csv.CSVRecord;

/**
 * TODO mcj update with updates from data model
 * 
 * @author johannes.najjar@tngtech.com
 */
public class ComponentAttachmentCSVRecordBuilder extends CustomizedCSVRecordBuilder<ComponentAttachmentCSVRecord> {

    private String componentName;
    private String releaseName;
    private String releaseVersion;
    private String attachmentContentId;
    private String filename;
    private String attachmentType;
    private String comment;
    private String createdOn;
    private String createdBy;

     ComponentAttachmentCSVRecordBuilder(CSVRecord record) {
        //parse CSV Record
        int i = 0;
        componentName = record.get(i++);
        releaseName = record.get(i++);
        releaseVersion = record.get(i++);
        attachmentContentId = record.get(i++);
        filename = record.get(i++);
        attachmentType = record.get(i++);
        comment = record.get(i++);
        createdOn = record.get(i++);
        createdBy = record.get(i);
    }

    ComponentAttachmentCSVRecordBuilder() {
        componentName = null;
        releaseName = null;
        releaseVersion = null;
        attachmentContentId = null;
        filename = null;
        attachmentType = null;
        comment = null;
        createdOn = null;
        createdBy = null;
    }

    @Override
    public ComponentAttachmentCSVRecord build() {
        return new ComponentAttachmentCSVRecord(componentName, releaseName, releaseVersion,
                attachmentContentId, filename, attachmentType,
                comment, createdOn, createdBy);
    }

    public void fill(Attachment attachment) {
        setAttachmentContentId(attachment.getAttachmentContentId());
        setFilename(attachment.getFilename());

        if(attachment.isSetAttachmentType()) setAttachmentType(attachment.getAttachmentType().name());
        if(attachment.isSetCreatedComment()) setCreatorComment(attachment.getCreatedComment());
        if(attachment.isSetCreatedBy()) setCreatedBy(attachment.getCreatedBy());
        if(attachment.isSetCreatedOn()) setCreatedOn(attachment.getCreatedOn());
    }


    public void fill(Component component) {
        setComponentName(component.getName());
    }

    public void fill (Release release) {
        setReleaseName(release.getName());
        setReleaseVersion(release.getVersion());
    }


    //Autogenerated setters
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public void setAttachmentContentId(String attachmentContentId) {
        this.attachmentContentId = attachmentContentId;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public void setCreatorComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

/*
 * Copyright Siemens AG, 2014-2018. Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.sw360.datahandler.thrift;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.eclipse.sw360.datahandler.thrift.attachments.AttachmentContent;
import org.eclipse.sw360.datahandler.thrift.components.Component;
import org.eclipse.sw360.datahandler.thrift.components.ECCStatus;
import org.eclipse.sw360.datahandler.thrift.components.EccInformation;
import org.eclipse.sw360.datahandler.thrift.components.Release;
import org.eclipse.sw360.datahandler.thrift.licenses.*;
import org.eclipse.sw360.datahandler.thrift.projects.Project;
import org.eclipse.sw360.datahandler.thrift.users.User;
import org.eclipse.sw360.datahandler.thrift.vendors.Vendor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Predicates.notNull;
import static org.eclipse.sw360.datahandler.common.SW360Assert.*;
import static org.eclipse.sw360.datahandler.common.SW360Constants.*;
import static org.eclipse.sw360.datahandler.common.SW360Utils.newDefaultEccInformation;

/**
 * Utility class to validate the data before inserting it in the database.
 * In particular, in ensure the that the type is set correctly on the object (for easier parsing)
 *
 * @author cedric.bodet@tngtech.com
 */
public class ThriftValidate {

    private ThriftValidate() {
        // Utility class with only static functions
    }

    public static void prepareObligation(Obligation obligation) throws SW360Exception {
        // Check required fields
        assertNotNull(obligation);
        assertNotEmpty(obligation.getName());
        assertNotNull(obligation.getObligationId());

        // Check type
        obligation.setType(TYPE_OBLIGATION);
    }

    public static void prepareTodo(Todo todo) throws SW360Exception {
        // Check required fields
        assertNotNull(todo);
        assertNotEmpty(todo.getText());
        assertNotNull(todo.getTodoId());

        if (todo.isSetObligations() && !todo.isSetObligationDatabaseIds()) {
            for (Obligation obligation : todo.getObligations()) {
                todo.addToObligationDatabaseIds(obligation.getId());
            }
        }

        if (todo.obligations == null) {
            todo.setObligations(Collections.emptyList());
        }

        if (todo.whitelist == null) {
            todo.setWhitelist(Collections.emptySet());
        }

        todo.unsetObligations();
        // Check type
        todo.setType(TYPE_TODO);
    }

    public static void prepareRiskCategory(RiskCategory riskCategory) throws SW360Exception {
        // Check required fields
        assertNotNull(riskCategory);
        assertNotEmpty(riskCategory.getText());
        assertNotNull(riskCategory.getRiskCategoryId());

        // Check type
        riskCategory.setType(TYPE_RISKCATEGORY);
    }

    public static void prepareRisk(Risk risk) throws SW360Exception {
        // Check required fields
        assertNotNull(risk);
        assertNotEmpty(risk.getText());
        assertNotNull(risk.getRiskId());

        if (risk.isSetCategory() && !risk.isSetRiskCategoryDatabaseId()) {
            risk.setRiskCategoryDatabaseId(risk.getCategory().getId());
        }

        risk.unsetCategory();

        // Check type
        risk.setType(TYPE_RISK);
    }

    public static void prepareLicense(License license) throws SW360Exception {
        // Check required fields
        assertNotNull(license);
        assertNotEmpty(license.getId());
        assertNotEmpty(license.getFullname());

        if (license.isSetLicenseType() && !license.isSetLicenseTypeDatabaseId()) {
            license.setLicenseTypeDatabaseId(license.getLicenseType().getId());
        }
        license.unsetLicenseType();

        if (license.isSetTodos() && license.isSetTodoDatabaseIds()) {
            for (Todo todo : license.getTodos()) {
                license.addToTodoDatabaseIds(todo.getId());
            }
        }
        license.unsetTodos();

        if (license.isSetRisks() && !license.isSetRiskDatabaseIds()) {
            for (Risk risk : license.getRisks()) {
                license.addToRiskDatabaseIds(risk.getId());
            }
        }
        license.unsetRisks();

        // Check type
        license.setType(TYPE_LICENSE);

        // Unset optionals
        license.unsetPermissions();
    }


    public static void prepareUser(User user) throws SW360Exception {
        // Check required fields
        assertNotEmpty(user.getEmail());
        // Set id to email, in order to have human readable database
        user.setId(user.getEmail());
        // Set type
        user.setType(TYPE_USER);
        // guarantee that `CommentMadeDuringModerationRequest` is never stored in the database as this is intended to be a transient field
        user.unsetCommentMadeDuringModerationRequest();
    }

    public static void prepareVendor(Vendor vendor) throws SW360Exception {
        // Check required fields
        assertNotEmpty(vendor.getShortname());
        assertNotEmpty(vendor.getFullname());

        // Check type
        vendor.setType(TYPE_VENDOR);
    }

    public static void prepareComponent(Component component) throws SW360Exception {
        // Check required fields
        assertNotEmpty(component.getName());

        // Check type
        component.setType(TYPE_COMPONENT);

        // Unset optionals
        component.unsetPermissions();

        // Unset fields that do not make sense
        component.unsetReleases();
    }

    public static List<Component> prepareComponents(Collection<Component> components) throws SW360Exception {

        return FluentIterable.from(components).transform(new Function<Component, Component>() {
            @Override
            public Component apply(Component input) {
                try {
                    prepareComponent(input);
                } catch (SW360Exception e) {
                    return null;
                }
                return input;
            }
        }).filter(notNull()).toList();

    }

    public static void prepareRelease(Release release) throws SW360Exception {
        // Check required fields
        assertNotEmpty(release.getName());
        assertNotEmpty(release.getVersion());
        assertNotEmpty(release.getComponentId());

        // Check type
        release.setType(TYPE_RELEASE);

        // Save vendor ID, not object
        if (release.isSetVendor()) {
            release.vendorId = release.vendor.id;
        }

        ensureEccInformationIsSet(release);

        // Unset optionals
        release.unsetPermissions();
        release.unsetVendor();
        release.unsetCreatorDepartment();
    }

    public static Release ensureEccInformationIsSet(Release release) {
        EccInformation eccInformation = release.isSetEccInformation() ? release.getEccInformation() : newDefaultEccInformation();
        if (!eccInformation.isSetEccStatus()){
            eccInformation.setEccStatus(ECCStatus.OPEN);
        }
        release.setEccInformation(eccInformation);
        return release;
    }

    public static List<Release> prepareReleases(Collection<Release> components) throws SW360Exception {

        return FluentIterable.from(components).transform(new Function<Release, Release>() {
            @Override
            public Release apply(Release input) {
                try {
                    prepareRelease(input);
                } catch (SW360Exception e) {
                    return null;
                }
                return input;
            }
        }).filter(notNull()).toList();

    }

    public static void validateAttachment(AttachmentContent attachment) throws SW360Exception {
        assertNotNull(attachment);
        if (attachment.isOnlyRemote()) {
            String remoteUrl = attachment.getRemoteUrl();
            assertValidUrl(remoteUrl);
        }

        attachment.setType(TYPE_ATTACHMENT);
    }

    public static void validateLicense(License license) throws SW360Exception {
        if (license.isSetId()) {
            assertId(license.getId());
        }

        if (license.isSetShortname()) {
            assertId(license.getShortname());
        }

        if (license.isSetId() && license.isSetShortname()) {
            String message = "license short name must be equal with license id";
            assertEquals(license.getId(), license.getShortname(), message);
        }
    }

    public static void prepareProject(Project project) throws SW360Exception {
        assertNotEmpty(project.getName());
        project.setType(TYPE_PROJECT);

        // Unset temporary fields
        project.unsetPermissions();
        project.unsetReleaseClearingStateSummary();
    }
}

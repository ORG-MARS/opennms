//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.netmgt.config.modelimport.ModelImport;
import org.opennms.web.svclayer.ManualProvisioningService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class EditProvisioningGroupController extends SimpleFormController {

    public static class TreeCommand {
        private String m_formPath;
        private String m_action;
        private Object m_formData;
        private String m_currentNode;
        private String m_groupName = "hardcoded";
        
        public String getAction() {
            return m_action;
        }
        public void setAction(String action) {
            m_action = action;
        }
        public String getGroupName() {
            return m_groupName;
        }
        public void setGroupName(String groupName) {
            m_groupName = groupName;
        }
        public Object getFormData() {
            return m_formData;
        }
        public void setFormData(Object importData) {
            m_formData = importData;
        }
        public String getFormPath() {
            return m_formPath;
        }
        public void setFormPath(String target) {
            m_formPath = target;
        }
        public String getCurrentNode() {
            return m_currentNode;
        }
        public void setCurrentNode(String node) {
            m_currentNode = node;
        }
        public String getDataPath() {
            //added nodeEditForm. to the formData. because somehow we are getting that attached a prefix as well. 
            return m_formPath.substring("nodeEditForm.formData.".length());
        }
        public void setDataPath(String path) {
            //added nodeEditForm. to the formData. because somehow we are getting that attached a prefix as well.
            m_formPath = "nodeEditForm.formData."+path;
        }
    }

    private ManualProvisioningService m_provisioningService;

    public void setProvisioningService(ManualProvisioningService provisioningService) {
        m_provisioningService = provisioningService;
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.handleRequestInternal(request, response);
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        TreeCommand treeCmd = (TreeCommand)command;
        String action = treeCmd.getAction();
        if (action == null) {
            return doShow(request, response, treeCmd, errors);
        } else if ("addNode".equalsIgnoreCase(action)) {
            return doAddNode(request, response, treeCmd, errors);
        } else if ("addInterface".equalsIgnoreCase(action)) {
            return doAddInterface(request, response, treeCmd, errors);
        } else if ("addService".equalsIgnoreCase(action)) {
            return doAddService(request, response, treeCmd, errors);
        } else if ("addCategory".equalsIgnoreCase(action)) {
            return doAddCategory(request, response, treeCmd, errors);
        } else if ("addAssetField".equalsIgnoreCase(action)) {
            return doAddAssetField(request, response, treeCmd, errors);
        } else if ("save".equalsIgnoreCase(action)) {
            return doSave(request, response, treeCmd, errors);
        } else if ("edit".equalsIgnoreCase(action)) {
            return doEdit(request, response, treeCmd, errors);
        } else if ("cancel".equalsIgnoreCase(action)) {
            return doCancel(request, response, treeCmd, errors);
        } else if ("delete".equalsIgnoreCase(action)) {
            return doDelete(request, response, treeCmd, errors);
        } else if ("import".equalsIgnoreCase(action)) {
            return doImport(request, response, treeCmd, errors);
        } else if ("done".equalsIgnoreCase(action)) {
            return done(request, response, treeCmd, errors);
        } else {
            errors.reject("Unrecognized action: "+action);
            return showForm(request, response, errors);
        }
        
    }

    private ModelAndView done(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        return new ModelAndView(getSuccessView());
    }

    private ModelAndView doShow(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        return showForm(request, response, errors);
    }

    private ModelAndView doCancel(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {

        ModelImport formData = m_provisioningService.getProvisioningGroup(treeCmd.getGroupName());
        treeCmd.setFormData(formData);
        
        treeCmd.setCurrentNode("");
        
        return showForm(request, response, errors);
    }

    private ModelAndView doImport(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {

        m_provisioningService.importProvisioningGroup(treeCmd.getGroupName());
        return super.showForm(request, response, errors);
        
    }

    private ModelAndView doDelete(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        
        ModelImport formData = m_provisioningService.deletePath(treeCmd.getGroupName(), treeCmd.getDataPath());
        treeCmd.setFormData(formData);
        
        return showForm(request, response, errors);
    }

    private ModelAndView doAddCategory(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        
        ModelImport formData = m_provisioningService.addCategoryToNode(treeCmd.getGroupName(), treeCmd.getDataPath(), "New Category");
        treeCmd.setFormData(formData);
        
        treeCmd.setCurrentNode(treeCmd.getFormPath()+".category[0]");
        
        
        return showForm(request, response, errors);
    }

    private ModelAndView doAddAssetField(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        ModelImport formData = m_provisioningService.addAssetFieldToNode(treeCmd.getGroupName(), treeCmd.getDataPath(), "key", "value");
        treeCmd.setFormData(formData);
        
        treeCmd.setCurrentNode(treeCmd.getFormPath()+".asset[0]");
        
        return showForm(request, response, errors);
    }
    
    private ModelAndView doEdit(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        
        treeCmd.setCurrentNode(treeCmd.getFormPath());
        
        return showForm(request, response, errors);
    }

    private ModelAndView doSave(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {

        ModelImport formData = m_provisioningService.saveProvisioningGroup(treeCmd.getGroupName(), (ModelImport)treeCmd.getFormData());
        treeCmd.setFormData(formData);
        treeCmd.setCurrentNode("");
        return showForm(request, response, errors);
    }

    private ModelAndView doAddService(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {

        ModelImport formData = m_provisioningService.addServiceToInterface(treeCmd.getGroupName(), treeCmd.getDataPath(), "SVC");
        treeCmd.setFormData(formData);
        
        treeCmd.setCurrentNode(treeCmd.getFormPath()+".monitoredService[0]");
        
        
        return showForm(request, response, errors);
    }

    private ModelAndView doAddInterface(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {
        
        ModelImport formData = m_provisioningService.addInterfaceToNode(treeCmd.getGroupName(), treeCmd.getDataPath(), "1.1.1.1");
        treeCmd.setFormData(formData);
        
        treeCmd.setCurrentNode(treeCmd.getFormPath()+".interface[0]");
        
        
        return showForm(request, response, errors);
    }

    private ModelAndView doAddNode(HttpServletRequest request, HttpServletResponse response, TreeCommand treeCmd, BindException errors) throws Exception {

        treeCmd.setFormData(m_provisioningService.addNewNodeToGroup(treeCmd.getGroupName(), "New Node"));
        
        treeCmd.setCurrentNode("formData.node[0]");

        return showForm(request, response, errors);
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        TreeCommand formCommand = new TreeCommand();
        initializeTreeCommand(request, formCommand);
        return formCommand;
    }

    private void initializeTreeCommand(HttpServletRequest request, TreeCommand formCommand) throws Exception {
        String groupName = request.getParameter("groupName");
        if (groupName == null) {
            throw new IllegalArgumentException("groupName required");
        }
        
        ModelImport formData = m_provisioningService.getProvisioningGroup(groupName);
        if (formData == null) {
            formData = m_provisioningService.createProvisioningGroup(groupName);
        }
        
        formCommand.setFormData(formData);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        
        String[] choices = { "P", "S", "C", "N" };
        map.put("snmpPrimaryChoices", choices);
        
        map.put("categories", m_provisioningService.getNodeCategoryNames());
        map.put("assetFields", m_provisioningService.getAssetFieldNames());
        map.put("services",  m_provisioningService.getServiceTypeNames());
        
        
        return map;
    }
    
}

package ua.netcrackerteam.DAO;

import ua.netcrackerteam.DAO.Entities.*;
import ua.netcrackerteam.controller.bean.DifferenceData;
import ua.netcrackerteam.controller.bean.StudentsMarks;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kushnirenko Anna, Filipenko Aleksey
 */
public class DAOHRImpl extends DAOCoreObject implements DAOHR {

    public static int ID_USER_CATEGORY_HR = 2;

    @Override
    public List<Form> search(String category, String value) {
        String query = "";
        List listOfParam = new ArrayList();
        value = '%'+value+'%';
        beginTransaction();
        if (category.equals("institute")) {
            listOfParam.add(value);
            query = "from Form where status = 1 and interview is not null and upper(cathedra.faculty.institute.name) like upper(:param0)";
        }
        else if (category.equals("faculty")) {
            listOfParam.add(value);
            query = "from Form where status = 1 and interview is not null and upper(cathedra.faculty.name) like upper(:param0)";
        }
        else if (category.equals("cathedra")) {
            listOfParam.add(value);
            query = "from Form where status = 1 and interview is not null and upper(cathedra.name) like upper(:param0)";
        }
        else {
            listOfParam.add(category);
            listOfParam.add(value);
            query = "from Form where status = 1 and upper(:param0) like upper(:param1)";
        }
        List<Form> formList = super.<Form>executeListGetQuery(query, listOfParam);
        commitTransaction();
        return formList;
    }
    
    @Override
    public void setHRMark(int selectedFormID, String insertedMark, String userNameHR) {
        String query                        = "";
        InterviewRes currInterviewResult    = null;
        Form currFormForInsertResults       = null;
        UserList currHRUser                 = null;
        beginTransaction();
        List listOfParams = new ArrayList();
        listOfParams.add(selectedFormID);
        listOfParams.add(userNameHR);
        query = "from InterviewRes where form.formId = :param0 and user.userName = :param1";
        currInterviewResult = super.<InterviewRes>executeSingleGetQuery(query, listOfParams);
        listOfParams.clear();
        if (currInterviewResult != null) {
            currInterviewResult.setScore(insertedMark);
            super.saveUpdatedObject(currInterviewResult);
        }
        else {
            currInterviewResult = new InterviewRes();
            listOfParams.add(selectedFormID);
            query = "from Form where to_char(idForm) = to_char(:param0)";
            currFormForInsertResults = super.<Form>executeSingleGetQuery(query, listOfParams);
            currInterviewResult.setForm(currFormForInsertResults);
            listOfParams.clear();
            query = "from UserList where userName = :param0";
            listOfParams.add(userNameHR);
            currHRUser = super.<UserList>executeSingleGetQuery(query, listOfParams);
            currInterviewResult.setIdUser(currHRUser);
            currInterviewResult.setScore(insertedMark);
            super.saveUpdatedObject(currInterviewResult);
        }
        commitTransaction();

    }

    public UserList getUserDataByFormId(int formId) {

        String query            = "";
        List listOfParams       = new ArrayList();
        Form userForm           = null;
        UserList selectedUser   = null;
        beginTransaction();
        query = "from Form where to_char(idForm) = to_char(:param0)";
        listOfParams.add(formId);
        userForm = super.<Form>executeSingleGetQuery(query, listOfParams);
        selectedUser = userForm.getUser();
        commitTransaction();
        return selectedUser;
    }
 
    @Override
    public List<Form> getAllRegisteredForms() {
        String query                = "";
        List<Form> selectedForms    = null;
        beginTransaction();
        query = "from Form where status = 1 and interview is not null";
        selectedForms = super.<Form>executeListGetQuery(query);
        commitTransaction();
        return selectedForms;
    }
    
    public Long getAllRegisteredFormsCount() {
        String query                = "";
        beginTransaction();
        query = "select count(*) from Form where status = 1 and interview is not null";
        Long count = executeSingleGetQuery(query);
        commitTransaction();
        return count;
    }

    public List<HrTempInfo> getHrTempInfo() {
        String query                = "";
        List<HrTempInfo> selectedInfo     = null;
        beginTransaction();
        query = "from HrTempInfo";
        selectedInfo = super.<HrTempInfo>executeListGetQuery(query);
        commitTransaction();
        return selectedInfo;
    }

    public HrTempInfo getHrTempInfoByFormID(int formID) {
        String query                = "";
        HrTempInfo selectedInfo     = null;
        List listOfParams           = new ArrayList();
        beginTransaction();
        query = "from HrTempInfo where to_char(form.idForm) = to_char(:param0)";
        listOfParams.add(formID);
        selectedInfo = super.<HrTempInfo>executeSingleGetQuery(query, listOfParams);
        commitTransaction();
        return selectedInfo;
    }

    public void setHrTempInfo(HrTempInfo hrTempInfo){
        beginTransaction();
        super.<HrTempInfo>saveUpdatedObject(hrTempInfo);
        commitTransaction();
    }

    /*public void updateHrTempInfo(HrTempInfo hrTempInfo){
        *//*Session session = null;
        Transaction transaction = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            session.update(hrTempInfo);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*//*

    }*/

    public void deleteHrTempInfo(int tempInfoID){
        String query        = "";
        List listOfParam    = new ArrayList();
        HrTempInfo currHRInfo = null;
        beginTransaction();
        query = "from HrTempInfo where to_char(idHrTempInfo) = to_char(:param0)";
        currHRInfo = super.<HrTempInfo>executeSingleGetQuery(query);
        super.<HrTempInfo>executeDeleteQuery(currHRInfo);
        commitTransaction();
    }

    public HrTempInfo getHrTempInfoByID(int tempInfoID){
        String query        = "";
        List listOfParam    = new ArrayList();
        HrTempInfo currHRInfo = null;
        listOfParam.add(tempInfoID);
        beginTransaction();
        query = "from HrTempInfo where to_char(idHrTempInfo) = to_char(:param0)";
        currHRInfo = super.<HrTempInfo>executeSingleGetQuery(query, listOfParam);
        return currHRInfo;
    }

    @Override
    public List<Form> getNonVerificatedForms() {
        String queryForm        = "";
        List listOfParams       = new ArrayList();
        List<Form> listOfForms  = null;
        beginTransaction();
        listOfParams.add(5);
        queryForm = "from Form where to_char(status) = to_char(:param0)";
        listOfForms = super.<Form>executeListGetQuery(queryForm, listOfParams);
        commitTransaction();
        return listOfForms;
    }
    
    public Long getNonVerificatedFormsCount() {
        String queryForm        = "";
        beginTransaction();
        queryForm = "select count(*) from Form where id_status is 5";
        Long count = executeSingleGetQuery(queryForm);
        commitTransaction();
        return count;
    }

    public List<Form> getBlanksWithoutInterview() {
        String queryForm        = "";
        List<Form> listOfForms  = null;
        beginTransaction();
        queryForm = "from Form where interview is null";
        listOfForms = super.<Form>executeListGetQuery(queryForm);
        commitTransaction();
        return listOfForms;
    }
    
    public Long getBlanksWithoutInterviewCount() {
        String queryForm        = "";
        beginTransaction();
        queryForm = "select count(*) from Form where interview is null";
        Long count = executeSingleGetQuery(queryForm);
        commitTransaction();
        return count;
    }

    @Override
    public void verificateForm(int formID) {
        String query            = "";
        Status currStatus       = null;
        List listOfParams       = new ArrayList();
        Form currForm           = null;
        beginTransaction();
        query = "from Status where idStatus = 1";
        currStatus = super.<Status>executeSingleGetQuery(query);
        listOfParams.add(formID);
        query = "from Form where to_char(idForm) = to_char(:param0)";
        currForm = super.<Form>executeSingleGetQuery(query, listOfParams);
        UserList currUser = currForm.getUser();
        listOfParams.clear();
        query = "from Form where user = :param0 and status = :param1";
        listOfParams.add(currUser);
        listOfParams.add(currStatus);
        Form oldForm = super.<Form>executeSingleGetQuery(query, listOfParams);
        super.<Form>executeDeleteQuery(oldForm);
        currForm.setStatus(currStatus);
        super.<Form>updatedObject(currForm);
        commitTransaction();
    }

    @Override
    public void deleteForm(int formID) {
        String query            = "";
        List listOfParams       = new ArrayList();
        Form currForm           = null;
        beginTransaction();
        query = "from Form where to_char(idForm) = to_char(:param0)";
        listOfParams.add(formID);
        currForm = super.<Form>executeSingleGetQuery(query, listOfParams);
        super.<Form>executeDeleteQuery(currForm);
        commitTransaction();
    }

    @Override
    public void setStudentAttendStatus(int statusID, int formID) {
        /*Session session = null;
        Query query = null;
        Transaction transaction = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            query = session.createQuery("from Form where idForm = " + formID);
            Form selectedForm = (Form) query.uniqueResult();
            query = session.createQuery("from Status where idStatus = " + statusID);
            Status status = (Status) query.uniqueResult();
            selectedForm.setStatusAttend(status);
            session.save(selectedForm);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*/
        String query            = "";
        List listOfParams       = new ArrayList();
        beginTransaction();
        listOfParams.add(formID);
        query = "from Form where to_char(idForm) = to_char(:param0)";
        Form selectedForm = super.<Form>executeSingleGetQuery(query, listOfParams);
        listOfParams.clear();
        query = "from Status where to_char(idStatus) = to_char(:param0)";
        listOfParams.add(statusID);
        Status selectedStatus = super.<Status>executeSingleGetQuery(query,listOfParams);
        selectedForm.setStatusAttend(selectedStatus);
        super.<Form>updatedObject(selectedForm);
        commitTransaction();

    }

    @Override
    public List<InterviewRes> getInterviewersMarks(int selectedFormID) {
        /*Session session = null;
        Query query;                
        List<InterviewRes> marks = null;        
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();    
            query = session.createQuery("from InterviewRes"
                    + " where form = " + selectedFormID + " and user IN "
                    + "(select idUser from UserList where idUserCategory = 3)");
            marks = query.list();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return marks;*/
        String query            = "";
        List listOfParams       = new ArrayList();
        beginTransaction();
        listOfParams.add(selectedFormID);
        query = "from InterviewRes where to_char(form) = to_char(:param0) and user IN (select idUser from UserList where idUserCategory = 3)";
        List<InterviewRes> marks = super.<InterviewRes>executeListGetSQLQuery(query, listOfParams);
        commitTransaction();
        return marks;
    }

    public List<InterviewRes> getAllStudentsMarks(int selectedFormID) {
        /*Session session = null;
        Query query;
        List<InterviewRes> marks = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            query = session.createQuery("from InterviewRes"
                    + " where form = " + selectedFormID + " and user IN "
                    + "(select idUser from UserList where (idUserCategory = 3) or (idUserCategory = 2))");
            marks = query.list();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return marks;*/
        String query            = "";
        List listOfParams       = new ArrayList();
        beginTransaction();
        listOfParams.add(selectedFormID);
        query = "from InterviewRes where to_char(form) = to_char(:param0) and user IN (select idUser from UserList where (idUserCategory = 3) or (idUserCategory = 2))";
        List<InterviewRes> marks = super.<InterviewRes>executeListGetQuery(query, listOfParams);
        commitTransaction();
        return marks;

    }

    @Override
    public String getInterviewerNameByID(int userID) {
        /*Session session = null;
        Query query;
        String name = "";        
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();    
            query = session.createQuery("from UserList"
                    + " where idUser = " + userID);
            UserList ul = (UserList) query.uniqueResult();
            name = ul.getUserName();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return name;*/
        String query            = "";
        List listOfParams       = new ArrayList();
        String name             = "";
        beginTransaction();
        listOfParams.add(userID);
        query = "from UserList where to_char(idUser) = to_char(:param0)";
        UserList currUser = super.<UserList>executeSingleGetQuery(query, listOfParams);
        name = currUser.getUserName();
        commitTransaction();
        return name;

    }

    public void addNewInterview(Interview newInterview) {
        /*Session session = null;
        Transaction transaction = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            session.save(newInterview);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*/
        beginTransaction();
        super.<Interview>saveUpdatedObject(newInterview);
        commitTransaction();
    }

    @Override
    public void deleteInterview(int interviewId) {

        String query = "";
        List listOfParams       = new ArrayList();
        beginTransaction();
        listOfParams.add(interviewId);
        query = "from Form where to_char(interview) = to_char(:param0)";
        List<Form> listOfForms = super.<Form>executeListGetSQLQuery(query, listOfParams);
        query = "from Interview where reserve = 1";
        Interview nullInterview = super.<Interview>executeSingleGetQuery(query);
        for(Form currForm:listOfForms) {
            currForm.setInterview(nullInterview);
        }
        listOfParams.clear();
        listOfParams.add(interviewId);
        query = "from Interview where to_char(idInterview) = to_char(:param0)";
        Interview selectedInterview = super.<Interview>executeSingleGetQuery(query, listOfParams);
        super.<Interview>executeDeleteQuery(selectedInterview);
        commitTransaction();

    }

    public void editInterview(Interview interview) {
        /*Session session = null;
        Transaction transaction = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();   
            session.update(interview);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*/
        beginTransaction();
        super.<Interview>updatedObject(interview);
        commitTransaction();

    }
    
    
    //Maksym added here bellow
    
    public Institute addInstitute(String instituteName) {
       /* Session session = null;
        Transaction transaction = null;
        Institute institute = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            institute = new Institute();
            institute.setName(instituteName);
            session.save(institute);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*/
        Institute newInstitute = null;
        beginTransaction();
        newInstitute = new Institute();
        newInstitute.setName(instituteName);
        super.<Institute>saveUpdatedObject(newInstitute);
        commitTransaction();
        return newInstitute;
    }
    
    
    public Faculty addFaculty(Institute institute, String facultyName) {
        /*Session session = null;
        Transaction transaction = null;
        Faculty faculty = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            faculty = new Faculty();
            faculty.setInstitute(institute);
            faculty.setName(facultyName);            
            session.save(faculty);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return faculty;*/
        Faculty newFaculty = null;
        beginTransaction();
        newFaculty = new Faculty();
        newFaculty.setName(facultyName);
        newFaculty.setInstitute(institute);
        super.<Faculty>saveUpdatedObject(newFaculty);
        commitTransaction();
        return newFaculty;
    }
    
    
    public Cathedra addCathedra(Faculty faculty, String cathedraName) {
        /*Session session = null;
        Transaction transaction = null;
        Cathedra cathedra = null;
        try {
            Locale.setDefault(Locale.ENGLISH);
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            cathedra = new Cathedra();
            cathedra.setFaculty(faculty);
            cathedra.setName(cathedraName);
            session.save(cathedra);
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return cathedra;*/
        Cathedra newCathedra = null;
        beginTransaction();
        newCathedra = new Cathedra();
        newCathedra.setName(cathedraName);
        newCathedra.setFaculty(faculty);
        super.<Cathedra>saveUpdatedObject(newCathedra);
        commitTransaction();
        return newCathedra;
        
    }

    public Form getForm(int currFormId) {
        Form currForm = null;
        String query = "";
        List listOfParams = new LinkedList();
        beginTransaction();
        query = "from Form where to_char(idForm) = to_char(:param0)";
        listOfParams.add(currFormId);
        currForm = super.<Form>executeSingleGetQuery(query,listOfParams);
        return currForm;
    }

    public void setHRMark(StudentsMarks currMark, int idForm) {

        DAOCommonImpl currDAOCommon = new DAOCommonImpl();
        boolean marksUpdate = !(currDAOCommon.getStudentMark(idForm, ID_USER_CATEGORY_HR) == null);


    }

    public List<DifferenceData> getDiff(int currUserId) {

        Hashtable tableOfNames = new Hashtable();
        tableOfNames.put("ID_FORM",                 "Номер анкеты");
        tableOfNames.put("FIRST_NAME",              "Фамилия");
        tableOfNames.put("LAST_NAME",               "Имя");
        tableOfNames.put("MIDDLE_NAME",             "Отчество");
        tableOfNames.put("EXEC_PROJECT",            "Выполненные проекты");
        tableOfNames.put("REASON",                  "Причина принятия в УЦ");
        tableOfNames.put("EXTRA_INFO",              "Доп. информация");
        tableOfNames.put("INSTITUTE_YEAR",          "Курс");
        tableOfNames.put("INSTITUTE_GRAD_YEAR",     "Год окончания ВУЗа");
        tableOfNames.put("EXTRA_KNOWLEDGE",         "Доп. знания");
        tableOfNames.put("INTEREST_STUDY",          "Заин-сть учебой");
        tableOfNames.put("INTEREST_WORK",           "Заин-сть работой");
        tableOfNames.put("INTEREST_BRANCH_SOFT",    "Заин-сть разработкой ПО");
        tableOfNames.put("INTEREST_OTHER",          "Заин-сть другим");
        tableOfNames.put("INTEREST_DEEP_SPEC",      "Заин-сть глубокой специализацией");
        tableOfNames.put("INTEREST_VARIOUS",        "Заин-сть разнообразной работой");
        tableOfNames.put("INTEREST_MANAGMENT",      "Заин-сть руководством");
        tableOfNames.put("INTEREST_SALE",           "Заин-сть продажами");

        //currUserId = 2300;
        String query = "";
        String queryText = "";
        List listOfParams       = new ArrayList();
        beginTransaction();
        query = "select column_name from ALL_TAB_COLUMNS where table_name = 'FORM'";
        List<String> listOfFields = super.<String>executeListGetSQLQuery(query, listOfParams);
        Boolean first = true;
        int k = 0;
        for (String colunmName : listOfFields) {
            if (colunmName.toLowerCase().contains("id") || colunmName.toLowerCase().contains("photo") ) {
                continue;
            }
            String currcolumnQuery = "select * from (select '" + colunmName + "', to_char(f1." + colunmName + "), to_char(f2." + colunmName + ")" +
                    "from form f1, form f2 where" +
                    "(f1.id_user = f2.id_user) and (f1.id_user = " + currUserId + " ) " +
                    "and (f1." + colunmName + "<> f2." + colunmName + ")) where rownum = 1";
            if (first) {
                queryText = currcolumnQuery;
                first = false;
            } else {
                queryText = queryText + " union " + currcolumnQuery;
            }
        }
        List <Object[]> columnData = super.<Object[]>executeListGetSQLQuery(queryText);

        List<DifferenceData> diffList = new LinkedList<DifferenceData>();
        for (Object[] columnValue:columnData) {
            String columnAlias  = (String)columnValue[0];
            String oldValue     = (String)columnValue[1];
            String newValue     = (String)columnValue[2];
            DifferenceData currDiffData = new DifferenceData();
            currDiffData.setFieldName((String) tableOfNames.get(columnAlias));
            currDiffData.setOldValue(oldValue);
            currDiffData.setNewValue(newValue);
            diffList.add(currDiffData);
        }

        commitTransaction();
        return diffList;
    }
}
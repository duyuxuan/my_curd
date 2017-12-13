package ${(meta.basePackageName)!}.${(meta.moduleName)!}.controller;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

import com.hxkj.common.constant.Constant;
import com.hxkj.common.util.BaseController;
import com.hxkj.common.util.Identities;
import com.hxkj.common.util.SearchSql;
import ${(meta.basePackageName)!}.${(meta.moduleName)!}.model.${(meta.claUpName)!};

/**
 * ${(meta.claUpName)!} 控制器
 */
public class ${(meta.claUpName)!}Controller extends BaseController{

        /**
         * 列表页
         */
        public void index(){
          render("${(meta.moduleName)!}/${(meta.claLowName)!}.html");
        }


        /**
         * 列表数据
         */
        @Before(SearchSql.class)
        public void query(){
            int pageNumber=getAttr("pageNumber");
            int pageSize=getAttr("pageSize");
            String where=getAttr(Constant.SEARCH_SQL);
            Page<${(meta.claUpName)!}>${(meta.claLowName)!}Page=${(meta.claUpName)!}.dao.page(pageNumber,pageSize,where);

            renderDatagrid(${(meta.claLowName)!}Page);
        }


        /**
         * 打开新增或者修改弹出框
         */
        public void newModel(){
            String id=getPara("id");
            if(id!=null){
            ${(meta.claUpName)!} ${(meta.claLowName)!}=${(meta.claUpName)!}.dao.findById(id);
            setAttr("${(meta.claLowName)!}",${(meta.claLowName)!});
            }

            render("${(meta.moduleName)!}/${(meta.claLowName)!}_form.html");
        }


        /**
         * 增加
         */
        public void addAction(){

            ${(meta.claUpName)!} ${(meta.claLowName)!}=getBean(${(meta.claUpName)!}.class,"");
            ${(meta.claLowName)!}.setId(Identities.uuid2());
            boolean saveFlag=${(meta.claLowName)!}.save();
            if(saveFlag){
                renderText(Constant.ADD_SUCCESS);
            }else{
                renderText(Constant.ADD_FAIL);
            }

        }

        /**
         * 删除
         */
        public void deleteAction(){
            String id=getPara("id");
            Boolean delflag=${(meta.claUpName)!}.dao.deleteById(id);
            if(delflag){
                renderText(Constant.DELETE_SUCCESS);
            }else{
                renderText(Constant.DELETE_FAIL);
            }
        }

        /**
         * 修改
         */
        public void updateAction(){
            ${(meta.claUpName)!} ${(meta.claLowName)!}=getBean(${(meta.claUpName)!}.class,"");
            boolean updateFlag=${(meta.claLowName)!}.update();
            if(updateFlag){
                renderText(Constant.ADD_SUCCESS);
            }else{
                renderText(Constant.ADD_FAIL);
            }
        }
}
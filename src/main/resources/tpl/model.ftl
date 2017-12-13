package ${(meta.basePackageName)!}.${(meta.moduleName)!}.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import ${(meta.basePackageName)!}.${(meta.moduleName)!}.model.base.Base${meta.claUpName};

/**
*  table name: ${(meta.tableName)!}   ${(meta.remarks)!}
*/
public class ${(meta.claUpName)!} extends Base${meta.claUpName}<${(meta.claUpName)!}> implements java.io.Serializable{

    private static final long serialVersionUID = 1L;
    public static final ${(meta.claUpName)!} dao = new ${(meta.claUpName)!}().dao();

    public Page<${(meta.claUpName)!}>  page(int pageNumber,int pageSize,String where ){

        String sqlSelect = " select * ";
        String sqlExceptSelect = " from ${(meta.tableName)!}  ";
        if (StrKit.notBlank(where)) {
            sqlExceptSelect += " where " + where;
        }

        return this.paginate(pageNumber,pageSize,sqlSelect,sqlExceptSelect);
    };

}
package ${(basePackageName)!}.${(moduleName)!}.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import ${(basePackageName)!}.${(moduleName)!}.model.base.Base${table.tableNameCamelFirstUp};

/**
 * model table: ${(table.tableName)!}   ${(table.tableComment)!}
 * @author
 * @date ${generateDate!}
 */
public class ${(table.tableNameCamelFirstUp)!} extends Base${table.tableNameCamelFirstUp}<${(table.tableNameCamelFirstUp)!}> implements java.io.Serializable{

    private static final long serialVersionUID = 1L;
    public static final ${(table.tableNameCamelFirstUp)!} dao = new ${(table.tableNameCamelFirstUp)!}().dao();

    public Page<${(table.tableNameCamelFirstUp)!}>  page(int pageNumber,int pageSize,String where ){
        String sqlSelect = " select * ";
        String sqlExceptSelect = " from ${(table.tableName)!}  ";
        if (StrKit.notBlank(where)) {
            sqlExceptSelect += " where " + where;
        }
        /*jfinal paginate 函数使用 select count() 语句，该函数在百万级别数据效率非常低，数据量大不应该使用jfinal分页封装 */
        return this.paginate(pageNumber,pageSize,sqlSelect,sqlExceptSelect);
    }

}

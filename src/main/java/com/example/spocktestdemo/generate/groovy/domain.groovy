package com.example.spocktestdemo.generate.groovy

import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import java.text.DateFormat

/**
 * domain生成
 * jin 2018/12/14 0014
 */
packageName = ""
tableName = ""
typeMapping = [
        (~/(?i)tinyint|smallint|mediumint/)      : "Integer",
        (~/(?i)int/)                             : "Long",
        (~/(?i)bool|bit/)                        : "Boolean",
        (~/(?i)float|double|decimal|real/)       : "Double",
        (~/(?i)datetime|timestamp|date|time/)    : "Date",
        (~/(?i)blob|binary|bfile|clob|raw|image/): "InputStream",
        (~/(?i)/)                                : "String"
]

FILES.chooseDirectoryAndSave("选择文件存放位置", "选择javabean对象") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    tableName = table.getName()
//    tableSimpleName = tableName.substring(tableName.indexOf("_") + 1);
    def className = javaName(tableName, true)
    def fields = calcFields(table)
    packageName = getPackageName(dir)
    new File(dir, className + ".java").withPrintWriter('utf-8') { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {
    out.println "package $packageName"
    out.println ""

    Set types = new HashSet()
    fields.each() {
/*        if (it.name == "id" || it.name == "gmtCreate" || it.name == "gmtModified") {
            return
        }*/
        types.add(it.type)
    }
    if (types.contains("Date")) {
        out.println "import java.util.Date;"
    }
    if (types.contains("InputStream")) {
        out.println "import java.io.InputStream;"
    }
    out.println "import lombok.Data;"
    out.println ""
    out.println "import java.io.Serializable;"
    out.println "import lombok.EqualsAndHashCode;"
    out.println "import lombok.experimental.Accessors;"
    out.println "import org.hibernate.annotations.CreationTimestamp;"
    out.println "import org.hibernate.annotations.UpdateTimestamp;"
    out.println ""
    out.println "import javax.persistence.Table;"
    out.println "import javax.persistence.GeneratedValue;"
    out.println "import javax.persistence.Id;"
    out.println "import javax.persistence.GenerationType;"
//    out.println "import org.hibernate.annotations.*;"
    out.println ""
    out.println "/**"
    out.println " * "
    String s = DateFormat.getDateInstance().format(new Date());
    out.println " * @author Gary " + s;
    out.println " */"
    out.println "@Data"
    out.println "@EqualsAndHashCode(callSuper = false)"
    out.println "@Accessors(chain = true)"
    out.println "@Entity"
    out.println "@Table(name = \"${tableName}\")"
    out.println "public class $className implements Serializable {"
    out.println ""
    out.println "\tprivate static final long serialVersionUID = 1L;"
    fields.each() {
/*        if (it.name == "id" || it.name == "gmtCreate" || it.name == "gmtModified"
                || it.name == "createTime" || it.name == "updateTime") {
            return
        }*/
        out.println ""
        // 输出注释
        if (isNotEmpty(it.commoent)) {
            out.println "\t/**"
            out.println "\t * ${it.commoent}"
            out.println "\t */"
        }
        if (it.name == "id"){
            out.println "\t@Id"
            out.println "\t@GeneratedValue(strategy = GenerationType.IDENTITY)"
        }
        if (it.name == "gmtCreate" || it.name == "createAt" || it.name == "createTime"){
            out.println "\t@CreationTimestamp"
        }
        if (it.name == "gmtModified" || it.name == "updateAt" || it.name == "updateTime"){
            out.println "\t@UpdateTimestamp"
        }
        out.println "\tprivate ${it.type} ${it.name};"
    }
    out.println ""
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name    : javaName(col.getName(), false),
                           type    : typeStr,
                           commoent: col.getComment(),
                           annos   : ""]
        ]
    }
}

def isNotEmpty(content) {
    return content != null && content.toString().trim().length() > 0
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

/**
 * 获取包名称
 * @param dir 实体类所在目录
 * @return
 */
def getPackageName(dir) {
    return dir.toString().replaceAll("\\/", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
}


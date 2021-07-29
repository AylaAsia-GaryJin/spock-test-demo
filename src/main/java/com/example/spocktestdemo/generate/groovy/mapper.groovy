package com.example.spocktestdemo.generate.groovy

import com.intellij.database.model.DasTable
import com.intellij.database.util.Case

import java.text.DateFormat

/**
 * mapper生成
 * Jin 2018/12/14 0014
 */
domainPackageName = "com.generator.demo.model"
domainName = ""
baseMapperLocation = "com.generator.demo.common.BaseMapper"

FILES.chooseDirectoryAndSave("选择文件存放位置", "选择javabean对象") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def tableName = table.getName()
//    domainName = tableName.substring(tableName.indexOf("_") + 1)
    domainName = tableName
    def className = javaName(domainName + "Mapper", true)
    domainName = javaName(domainName, true)
    packageName = getPackageName(dir)
    new File(dir, className + ".java").withPrintWriter('utf-8') { out -> generate(out, className, null) }
}

def generate(out, className, fields) {
    out.println "package $packageName"
    out.println ""
    out.println "import $baseMapperLocation;\n" +
            "import $domainPackageName.$domainName;"

    out.println ""
    out.println "/**"
    out.println " * "
    String time = DateFormat.getDateInstance().format(new Date());
    out.println " * @author Jin "
    out.println " * @create " + time;
    out.println " */"
    out.println "public interface $className extends BaseMapper<$domainName> {"
    out.println ""
    out.println "}"
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
 */
def getPackageName(dir) {
    return dir.toString().replaceAll("\\/", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
}

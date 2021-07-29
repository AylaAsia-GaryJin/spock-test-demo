package com.example.spocktestdemo.generate.groovy

import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/**
 * dao生成
 * jiansong 2018/12/14 0014
 */
tableName = ""
mapperPackageName = "com.generator.demo.mapper"
domainName = ""
packageName = ""

FILES.chooseDirectoryAndSave("选择文件存放位置", "选择javabean对象") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    tableName = table.getName()
//    domainName = tableName.substring(tableName.indexOf("_") + 1)
    domainName = tableName
    def className = javaName(domainName + "Mapper", true)
    domainName = javaName(domainName, true)
    def fields = calcFields(table)
    packageName = getPackageName(dir)
    new File(dir, className + ".xml").withPrintWriter('utf-8') { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {
    out.println "<?wtrjXml version=\"1.0\" encoding=\"UTF-8\" ?>"
    out.println "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >"
    out.println "<mapper namespace=\"${mapperPackageName}.${domainName}Mapper\">"
    out.println ""
    out.println "</mapper>"
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

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        fields += [[
                           name    : col.getName(),
                           annos   : ""]
        ]
    }
}


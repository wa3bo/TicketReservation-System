cmake_minimum_required(VERSION 3.5)
project(cinema LANGUAGES Java)
find_package(Java 1.7 REQUIRED COMPONENTS Development)
include(UseJava)
file(GLOB_RECURSE SOURCES "src/*.java")
file(GLOB_RECURSE JARS "ressources/sqlite/*.jar")
add_jar(gui ${SOURCES} MANIFEST ./ressources/sqlite/MANIFEST.MF INCLUDE_JARS ${JARS} ENTRY_POINT reservations.Gui)


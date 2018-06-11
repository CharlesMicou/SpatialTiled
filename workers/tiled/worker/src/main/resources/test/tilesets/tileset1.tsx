<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tileset1" tilewidth="32" tileheight="32" tilecount="8" columns="4">
 <image source="../img/unspaced_tiles_1.png" width="128" height="64"/>
 <tile id="0">
  <properties>
   <property name="test_string_property" value="blep"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="test_bool_property" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="test_int_property" type="int" value="1337"/>
  </properties>
 </tile>
 <tile id="3" type="tile_with_no_custom_properties"/>
 <tile id="4" type="test_tile_type"/>
 <tile id="5">
  <properties>
   <property name="test_file_property" type="file" value="../img/unspaced_tiles_1.png"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="test_float_property" type="float" value="-123.40000000000001"/>
  </properties>
 </tile>
 <tile id="7" type="multi_test_tile">
  <properties>
   <property name="test_int_property" type="int" value="42"/>
   <property name="test_string_property" value="abcde"/>
  </properties>
 </tile>
</tileset>

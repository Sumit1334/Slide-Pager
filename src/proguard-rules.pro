# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.sumit.slidepager.SlidePager {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/sumit/slidepager/repack'
-flattenpackagehierarchy
-dontpreverify

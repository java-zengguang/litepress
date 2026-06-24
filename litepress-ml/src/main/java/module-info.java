module litepress.ml {
    requires datavec.api;
    requires deeplearning4j.datasets;
    requires deeplearning4j.datavec.iterators;
    requires deeplearning4j.nn;
    requires encog.core;
    requires nd4j.api;
    requires org.tinylog.api;
    exports io.github.java_zengguang.litepress.ml;
    exports io.github.java_zengguang.litepress.ml.train;
    exports io.github.java_zengguang.litepress.ml.config;
    exports io.github.java_zengguang.litepress.ml.factory;

}
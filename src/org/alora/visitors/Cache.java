package org.alora.visitors;


import org.ethan.analyze.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

public class Cache extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("CacheableNode")) >= 1 || cn.fieldCount(desc("HashTable")) >= 1;
    }

    @Override
    public void visit() {
    }
}
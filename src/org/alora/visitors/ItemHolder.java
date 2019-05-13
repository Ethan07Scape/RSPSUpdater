package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"itemInstance"})
public class ItemHolder extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.superName.equals(clazz("CacheableNode"))) {
            if (cn.access == 48) {
                if (cn.getFieldTypeCount() == 1) {
                    return cn.methods.size() < 5;
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {

        for (ClassNode cn : getUpdateEngine().getClassNodes().values()) {
            for (FieldNode f : cn.fields) {
                if (f.desc.equals("L" + clazz("Item") + ";")) {
                    add(new FieldHookFrame("itemInstance", f));
                }

            }
        }
    }

}

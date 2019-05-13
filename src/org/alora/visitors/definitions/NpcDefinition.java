package org.alora.visitors.definitions;


import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.Searcher;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

@VisitorInfo(hooks = {"name", "actions", "id"})
public class NpcDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.fieldCount("Z") >= 4 && cn.fieldCount("Z") < 7 && cn.methods.size() > 15) {
            return true;
        }
        return false;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"));
        add("actions", getCn().getField(null, "[Ljava/lang/String;"));
        findID();

    }

    public void findID() {
        final MethodNode m = getFirstMethod(getCn(), methodNode -> methodNode.desc.equals("(L" + getCn().name + ";)L" + getCn().name + ";"));
        if (m != null) {
            Searcher search = new Searcher(m);
            FieldInsnNode fin = search.getField(search.findSingleOpcode(GETFIELD, 0));
            if (fin != null) {
                add(new FieldHookFrame("id", fin));
            }
        }
    }
}
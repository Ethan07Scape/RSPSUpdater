package org.alora;



import org.alora.visitors.Node;
import org.ethan.analyze.server.Server;
import org.ethan.analyze.server.ServerInfo;
import org.ethan.analyze.visitor.GraphVisitor;

@ServerInfo(serverName = "Alora", author = "Ethan", version = 1.0)
public class Core extends Server {

    public Core(String serverName) {
        super(serverName);
    }

    @Override
    protected GraphVisitor[] getVisitors() {
        return new GraphVisitor[]{
                new Node()
        };
    }

}
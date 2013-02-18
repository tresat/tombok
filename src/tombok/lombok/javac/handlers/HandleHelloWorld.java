package lombok.javac.handlers;

import java.lang.reflect.Modifier;

import lombok.HelloWorld;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;

import org.mangosdk.spi.ProviderFor;


import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

@ProviderFor(JavacAnnotationHandler.class)
public class HandleHelloWorld extends JavacAnnotationHandler<HelloWorld> {
	@Override
	public void handle(final AnnotationValues<HelloWorld> annotation, final JCAnnotation ast, final JavacNode annotationNode) {
		final JavacNode typeNode = annotationNode.up();
		
		if (notAClass(typeNode)) {
			annotationNode.addError("@HelloWorld is only supported on a class.");
		} else {
			final JCMethodDecl helloWorldMethod = createHelloWorld(typeNode);
			JavacHandlerUtil.injectMethod(typeNode, helloWorldMethod);
		}
	}
	
	private boolean notAClass(final JavacNode typeNode) {
		JCClassDecl typeDecl = null;
		if (typeNode.get() instanceof JCClassDecl) {
			typeDecl = (JCClassDecl)typeNode.get();
		}
		final long flags = typeDecl == null ? 0 : typeDecl.mods.flags;
		final boolean notAClass = typeDecl == null ||
				(flags & (Flags.INTERFACE | Flags.ENUM | Flags.ANNOTATION)) == 0;
		return notAClass;
	}
	
	private JCMethodDecl createHelloWorld(final JavacNode type) {
		final TreeMaker treeMaker = type.getTreeMaker();
		
		final JCModifiers           modifiers          = treeMaker.Modifiers(Modifier.PUBLIC);
		final List<JCTypeParameter> methodGenericTypes = List.<JCTypeParameter>nil();
		final JCExpression          methodType         = treeMaker.TypeIdent(TypeTags.VOID);
		final Name                  methodName         = type.toName("helloWorld");
		final List<JCVariableDecl>  methodParameters   = List.<JCVariableDecl>nil();
		final List<JCExpression>    methodThrows       = List.<JCExpression>nil();
		
		final JCExpression printlnMethod = JavacHandlerUtil.chainDots(type, "System", "out", "println");
		final List<JCExpression> printlnArgs = List.<JCExpression>of(treeMaker.Literal("hello world"));
		final JCMethodInvocation printlnInvocation =
				treeMaker.Apply(List.<JCExpression>nil(), printlnMethod, printlnArgs);
		final JCBlock methodBody =
				treeMaker.Block(0, List.<JCStatement>of(treeMaker.Exec(printlnInvocation)));
		
		final JCExpression defaultValue = null;
		
		return treeMaker.MethodDef(
				modifiers,
				methodName,
				methodType,
				methodGenericTypes,
				methodParameters,
				methodThrows,
				methodBody,
				defaultValue
				);
	}
}

package lombok.eclipse.handlers;

import static lombok.eclipse.Eclipse.ECLIPSE_DO_NOT_TOUCH_FLAG;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import lombok.HelloWorld;
import lombok.core.AnnotationValues;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.mangosdk.spi.ProviderFor;



@ProviderFor(EclipseAnnotationHandler.class)
public class HandleHelloWorld extends EclipseAnnotationHandler<HelloWorld> {
	//	@Override
	//	public void handle(final AnnotationValues<HelloWorld> annotation, final Annotation ast, final EclipseNode annotationNode) {
	//		final EclipseNode typeNode = annotationNode.up();
	//
	//		final FieldDeclaration fieldDecl = new FieldDeclaration("blah".toCharArray(), 0, -1);
	//
	//		EclipseHandlerUtil.injectField(typeNode, fieldDecl);
	//	}
	
	@Override
	public void handle(final AnnotationValues<HelloWorld> annotation, final Annotation ast, final EclipseNode annotationNode) {
		final EclipseNode typeNode = annotationNode.up();
		
		//		if (!isAClassNode(typeNode)) {
		//			annotationNode.addError("@HelloWorld is only supported on a class.");
		//		} else {
		final MethodDeclaration helloWorldMethod = createHelloWorld(typeNode, annotationNode, annotationNode.get(), ast);
		EclipseHandlerUtil.injectMethod(typeNode, helloWorldMethod);
		//typeNode.add(helloWorldMethod, Kind.METHOD);
		//		}
	}
	
	private boolean isAClassNode(final EclipseNode typeNode) {
		//		if (null == typeNode || typeNode.getKind() != lombok.core.AST.Kind.TYPE) {
		//			return false;
		//		}
		
		final int modifiers = ((TypeDeclaration) typeNode.get()).modifiers;
		return (modifiers & (ClassFileConstants.AccInterface | ClassFileConstants.AccAnnotation | ClassFileConstants.AccEnum)) != 0;
	}
	
	private MethodDeclaration createHelloWorld(final EclipseNode typeNode, final EclipseNode errorNode, final ASTNode astNode, final Annotation source) {
		final TypeDeclaration typeDecl = (TypeDeclaration) typeNode.get();
		
		final MethodDeclaration method = new MethodDeclaration(typeDecl.compilationResult);
		method.annotations = null;
		method.modifiers = Modifier.PUBLIC;
		method.typeParameters = null;
		method.returnType = new SingleTypeReference(TypeBinding.VOID.simpleName, 0);
		method.selector = "helloWorld".toCharArray();
		method.arguments = null;
		method.binding = null;
		method.thrownExceptions = null;
		method.bits |= ECLIPSE_DO_NOT_TOUCH_FLAG;
		
		final NameReference systemOutReference = createNameReference("System.out", source);
		final Expression [] printlnArguments = new Expression[] {
				new StringLiteral("Hello World".toCharArray(), astNode.sourceStart, astNode.sourceEnd, 0)
		};
		
		final MessageSend printlnInvocation = new MessageSend();
		printlnInvocation.arguments = printlnArguments;
		printlnInvocation.receiver = systemOutReference;
		printlnInvocation.selector = "println".toCharArray();
		
		method.bodyStart = method.declarationSourceStart = method.sourceStart = astNode.sourceStart;
		method.bodyEnd = method.declarationSourceEnd = method.sourceEnd = astNode.sourceEnd;
		method.statements = new Statement[] { printlnInvocation };
		return method;
	}
	
	private static NameReference createNameReference(final String name, final Annotation source) {
		final int pS = source.sourceStart, pE = source.sourceEnd;
		final long p = (long)pS << 32 | pE;
		
		final char[][] nameTokens = Eclipse.fromQualifiedName(name);
		final long[] pos = new long[nameTokens.length];
		Arrays.fill(pos, p);
		
		final QualifiedNameReference nameReference = new QualifiedNameReference(nameTokens, pos, pS, pE);
		nameReference.statementEnd = pE;
		
		return nameReference;
	}
}

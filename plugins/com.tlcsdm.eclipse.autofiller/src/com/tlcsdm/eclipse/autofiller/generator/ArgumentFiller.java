package com.tlcsdm.eclipse.autofiller.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ArgumentFiller {

	private final IDocument doc;
	private final int offset;
	private final ICompilationUnit icu;
	private int replaceOffset;
	private int replaceLength;

	public ArgumentFiller(IDocument doc, int offset, ICompilationUnit icu) {
		this.doc = doc;
		this.offset = offset;
		this.icu = icu;
	}

	public int getReplaceOffset() {
		return replaceOffset;
	}

	public int getReplaceLength() {
		return replaceLength;
	}

	/**
	 * Preview generated parameters without modifying the document
	 * 
	 * @return The parameter string, or null if the method call cannot be found
	 */
	public String previewArguments() {
		try {
			ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
			parser.setSource(icu);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			parser.setProject(icu.getJavaProject());

			CompilationUnit cu = (CompilationUnit) parser.createAST(null);

			MethodInvocationFinder finder = new MethodInvocationFinder(offset);
			cu.accept(finder);
			MethodInvocation invocation = finder.getMethodInvocation();

			if (invocation == null) {
				return null;
			}

			IMethodBinding binding = invocation.resolveMethodBinding();
			if (binding == null) {
				return null;
			}

			List<String> args = new ArrayList<>();

			// Try to get the parameter name
			IMethod method = (IMethod) binding.getJavaElement();
			if (method != null && method.exists()) {
				try {
					String[] paramNames = method.getParameterNames();
					args.addAll(Arrays.asList(paramNames));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// fallback: use type name
			if (args.isEmpty()) {
				for (ITypeBinding param : binding.getParameterTypes()) {
					args.add(param.getName());
				}
			}

			String replacement = String.join(", ", args);

			// Calculate the replacement range (in brackets)
			replaceOffset = invocation.getStartPosition() + invocation.getName().getLength() + 1;
			int end = invocation.getStartPosition() + invocation.getLength() - 1;
			replaceLength = end - replaceOffset;

			return replacement;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Modify the document directly
	 */
	public void fillArguments() {
		try {
			String replacement = previewArguments();
			if (replacement == null) {
				return;
			}
			doc.replace(replaceOffset, replaceLength, replacement);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find the MethodInvocation where the cursor is located
	 */
	static class MethodInvocationFinder extends ASTVisitor {

		private final int offset;
		private MethodInvocation target;

		MethodInvocationFinder(int offset) {
			this.offset = offset;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			// Include the method name and bracket scope
			int argStart = node.getName().getStartPosition();
			int argEnd = node.getStartPosition() + node.getLength();
			if (offset >= argStart && offset <= argEnd) {
				target = node;
			}

			return super.visit(node);
		}

		MethodInvocation getMethodInvocation() {
			return target;
		}
	}
}

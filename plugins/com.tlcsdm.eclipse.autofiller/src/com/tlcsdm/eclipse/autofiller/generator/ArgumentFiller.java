package com.tlcsdm.eclipse.autofiller.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
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

			ASTNode node = finder.getTargetNode();
			if (node == null) {
				return null;
			}

			IMethodBinding binding = null;

			if (node instanceof MethodInvocation mi) {
				binding = mi.resolveMethodBinding();
				replaceOffset = mi.getName().getStartPosition() + mi.getName().getLength() + 1;
				replaceLength = mi.getStartPosition() + mi.getLength() - 1 - replaceOffset;
			} else if (node instanceof SuperMethodInvocation smi) {
				binding = smi.resolveMethodBinding();
				replaceOffset = smi.getName().getStartPosition() + smi.getName().getLength() + 1;
				replaceLength = smi.getStartPosition() + smi.getLength() - 1 - replaceOffset;
			} else if (node instanceof ClassInstanceCreation cic) {
				binding = cic.resolveConstructorBinding();
				replaceOffset = cic.getStartPosition() + cic.getType().getStartPosition() + cic.getType().getLength()
						+ 1;
				replaceLength = cic.getStartPosition() + cic.getLength() - 1 - replaceOffset;
			} else {
				return null;
			}

			if (binding == null) {
				return null;
			}

			List<String> args = new ArrayList<>();

			// First try to get the parameter name in the source code
			IMethod method = (IMethod) binding.getJavaElement();
			if (method != null && method.exists()) {
				try {
					args.addAll(Arrays.asList(method.getParameterNames()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Fallback: Generate placeholder parameter names based on type
			if (args.isEmpty()) {
				int index = 1;
				for (ITypeBinding paramType : binding.getParameterTypes()) {
					String typeName = paramType.getName();
					if (typeName == null || typeName.isEmpty()) {
						typeName = "arg" + index++;
					} else {
						typeName = typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
					}
					args.add(typeName);
				}
			}

			return String.join(", ", args);

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
	 * ASTVisitor to find the target invocation node at the cursor
	 */
	static class MethodInvocationFinder extends ASTVisitor {

		private final int offset;
		private ASTNode target;

		MethodInvocationFinder(int offset) {
			this.offset = offset;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			if (inRange(node))
				target = node;
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			if (inRange(node))
				target = node;
			return super.visit(node);
		}

		@Override
		public boolean visit(ClassInstanceCreation node) {
			if (inRange(node))
				target = node;
			return super.visit(node);
		}

		private boolean inRange(ASTNode node) {
			int start = node.getStartPosition();
			int end = start + node.getLength();
			return offset >= start && offset <= end;
		}

		ASTNode getTargetNode() {
			return target;
		}
	}
}

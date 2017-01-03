package com.github.shuvigoss.zconf.web.utils.collection;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.github.shuvigoss.zconf.web.utils.Args.notNull;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class SimpleMultiwayTree<K, D> implements MultiwayTree<K, D> {

  static final List EMPTY = Lists.newLinkedList();

  CollectionUtil.Filter<D> filter = new CollectionUtil.Filter<D>() {
    @Override
    public D filter(Object source) {
      @SuppressWarnings("unchecked")
      TreeNode<K, D> node = (TreeNode<K, D>) source;
      return node.getData();
    }
  };

  TreeNode<K, D>         root;
  Collection<Node<K, D>> nodes;

  public SimpleMultiwayTree() {
    root = new TreeNode<>();
    nodes = Sets.newHashSet();
    nodes.add(root);
  }

  @Override
  public int size() {
    return nodes.size();
  }

  @Override
  public Node<K, D> add(K key, D data, Node<K, D> parent) {
    notNull(parent, "parent can not be null");
    TreeNode<K, D> n = (TreeNode<K, D>) parent;
    if (!isInTree(n))
      return null;
    TreeNode<K, D> node = new TreeNode<>(key, data, n);
    parent.addChildNode(node);
    nodes.add(node);
    return node;
  }

  protected boolean isInTree(TreeNode<K, D> parent) {
    return nodes.contains(parent);
  }

  @Override
  public boolean remove(Node<K, D> child) {
    TreeNode<K, D> node = (TreeNode<K, D>) child;
    if (node.isRoot() || !isInTree(node))
      return false;
    if (node.getParent().removeNode(node)) {
      nodes.remove(node);
      nodes.removeAll(node.getChildrenAll());
      return true;
    }
    return false;
  }

  @Override
  public int remove(K key) {
    int count = 0;
    Iterator<Node<K, D>> iterator = nodes.iterator();
    while (iterator.hasNext()) {
      Node<K, D> next = iterator.next();
      if (Objects.equals(next.getKey(), key)) {
        iterator.remove();
        count++;
      }
    }
    return count;
  }

  @Override
  public Collection<Node<K, D>> getNodes() {
    return Lists.newArrayList(nodes);
  }

  @Override
  public List<D> nodes() {
    return (List<D>) CollectionUtil.copy(nodes, filter);
  }

  @Override
  public Collection<Node<K, D>> getNodes(K key) {
    List<Node<K, D>> result = Lists.newLinkedList();
    for (Node<K, D> node : nodes) {
      if (Objects.equals(node.getKey(), key))
        result.addAll(node.getChildrenAll());
    }
    return result;
  }

  @Override
  public List<D> nodes(K key) {
    return (List<D>) CollectionUtil.copy(getNodes(key), filter);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Node<K, D>> getNodes(Node<K, D> node) {
    if (node == null)
      return EMPTY;
    if (!isInTree((TreeNode<K, D>) node))
      return EMPTY;
    return node.getChildrenAll();
  }

  @Override
  public List<D> nodes(Node<K, D> node) {
    return (List<D>) CollectionUtil.copy(getNodes(node), filter);
  }

  @Override
  public void clear() {
    nodes.clear();
    nodes.add(root);
  }

  @Override
  public TreeNode<K, D> getRoot() {
    return root;
  }

  @Override
  public Node<K, D> getNode(K key) {
    for (Node<K, D> node : nodes) {
      if (Objects.equals(node.getKey(), key))
        return node;
    }
    return null;
  }

  public class TreeNode<K1, D1> implements Node<K1, D1> {
    K1                       key;
    D1                       data;
    Node<K1, D1>             parent;
    Collection<Node<K1, D1>> children;

    protected TreeNode() {
      this.children = Lists.newLinkedList();
    }

    public TreeNode(K1 key, D1 data, TreeNode<K1, D1> parent) {
      notNull(key, "id can not be null");
      notNull(data, "data can not be null");
      notNull(parent, "parent can not be null");
      this.key = key;
      this.data = data;
      this.parent = parent;
      this.children = Lists.newLinkedList();
    }

    @Override
    public boolean isRoot() {
      return this == SimpleMultiwayTree.this.root;
    }

    @Override
    public boolean isLeaf() {
      return children.isEmpty();
    }

    @Override
    public K1 getKey() {
      return key;
    }

    @Override
    public D1 getData() {
      return data;
    }

    @Override
    public Node<K1, D1> getParent() {
      return parent;
    }

    @Override
    public void addChildNode(Node<K1, D1> child) {
      children.add(child);
    }

    @Override
    public boolean removeNode(Node<K1, D1> child) {
      return null != child.getParent() && child.getParent().getChildren().remove(child);
    }

    @Override
    public Collection<Node<K1, D1>> getChildren() {
      return children;
    }

    @Override
    public Collection<Node<K1, D1>> getChildrenAll() {
      if (children.isEmpty()) return children;
      List<Node<K1, D1>> all = Lists.newLinkedList();
      for (Node<K1, D1> node : children) {
        all.add(node);
        all.addAll(node.getChildrenAll());
      }
      return all;
    }
  }
}

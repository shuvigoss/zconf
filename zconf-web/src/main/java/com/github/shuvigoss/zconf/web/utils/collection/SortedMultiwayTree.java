package com.github.shuvigoss.zconf.web.utils.collection;

import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.TreeSet;

import static com.github.shuvigoss.zconf.web.utils.Args.isTrue;
import static com.github.shuvigoss.zconf.web.utils.Args.notNull;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class SortedMultiwayTree<K, D> extends SimpleMultiwayTree<K, D> {
  private Comparator<D> comparator;
  private Comparator<Node<K, D>> defaultComparator = new DefaultComparator();

  public SortedMultiwayTree(Comparator<D> comparator) {
    this();
    notNull(comparator, "comparator can not be null");
    this.comparator = comparator;
  }

  public SortedMultiwayTree() {
    root = new SortedTreeNode<>();
    nodes = Sets.newHashSet();
    nodes.add(root);
  }

  @Override
  public Node<K, D> add(K key, D data, Node<K, D> parent) {
    notNull(data, "data can not be null");
    isTrue(
        comparator != null || (data instanceof Comparable),
        "data may implements Comparable or comparator can not be null"
    );
    SortedTreeNode<K, D> n = (SortedTreeNode<K, D>) parent;
    if (!isInTree(n))
      return null;
    SortedTreeNode<K, D> node = new SortedTreeNode<>(key, data, n);
    parent.addChildNode(node);
    nodes.add(node);
    return node;
  }

  class DefaultComparator implements Comparator<Node<K, D>> {

    @Override
    public int compare(Node<K, D> o1, Node<K, D> o2) {
      if (o1.getData() instanceof Comparable)
        //noinspection unchecked
        return ((Comparable) o1.getData()).compareTo(o2.getData());
      else {
        Comparator<D> comparator = SortedMultiwayTree.this.comparator;
        if (comparator == null)
          throw new NullPointerException("comparator is null");
        return comparator.compare(o1.getData(), o2.getData());
      }
    }
  }

  public class SortedTreeNode<K1, D2> extends TreeNode<K1, D2> {
    protected SortedTreeNode() {
      //noinspection unchecked
      this.children = new TreeSet(SortedMultiwayTree.this.defaultComparator);
    }

    public SortedTreeNode(K1 key, D2 data, SortedTreeNode<K1, D2> parent) {
      this();
      notNull(key, "id can not be null");
      notNull(data, "data can not be null");
      notNull(parent, "parent can not be null");
      this.key = key;
      this.data = data;
      this.parent = parent;

    }
  }

}

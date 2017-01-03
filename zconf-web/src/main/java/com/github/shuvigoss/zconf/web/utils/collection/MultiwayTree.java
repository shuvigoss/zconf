package com.github.shuvigoss.zconf.web.utils.collection;

import java.util.Collection;
import java.util.List;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public interface MultiwayTree<K, D> {

  int size();

  Node<K, D> add(K key, D data, Node<K, D> parent);

  boolean remove(Node<K, D> child);

  int remove(K key);

  Collection<Node<K, D>> getNodes();

  List<D> nodes();

  Collection<Node<K, D>> getNodes(K key);

  List<D> nodes(K key);

  Collection<Node<K, D>> getNodes(Node<K, D> node);

  List<D> nodes(Node<K, D> node);

  void clear();

  Node<K, D> getRoot();

  Node<K, D> getNode(K key);

  interface Node<K, D> {

    boolean isRoot();

    boolean isLeaf();

    K getKey();

    D getData();

    Node<K, D> getParent();

    void addChildNode(Node<K, D> child);

    boolean removeNode(Node<K, D> child);

    Collection<Node<K, D>> getChildren();

    Collection<Node<K, D>> getChildrenAll();
  }

}

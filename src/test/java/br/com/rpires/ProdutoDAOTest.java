package br.com.rpires;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;

import br.com.rpires.dao.IProdutoDAO;
import br.com.rpires.dao.ProdutoDAO;
import br.com.rpires.dao.generic.jdbc.ConnectionFactory;
import br.com.rpires.dao.generic.jdbc.SchemaLoader;
import br.com.rpires.domain.Produto;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;

import static org.junit.Assert.*;

public class ProdutoDAOTest {

	private IProdutoDAO produtoDao;

	public ProdutoDAOTest() {
		produtoDao = new ProdutoDAO();
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		try (Connection conn = ConnectionFactory.getConnection()) {
			SchemaLoader.criarSchema(conn); // cria as tabelas TB_CLIENTE e TB_PRODUTO
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro criando o schema do banco");
		}
	}

	@After
	public void end() throws DAOException {
		Collection<Produto> list = produtoDao.buscarTodos();
		list.forEach(prod -> {
			try {
				produtoDao.excluir(prod.getCodigo());
			} catch (DAOException e) {
				e.printStackTrace();
			}
		});
	}

	private Produto criarProduto(String codigo) throws TipoChaveNaoEncontradaException, DAOException {
		Produto produto = new Produto();
		produto.setCodigo(codigo);
		produto.setDescricao("Produto 1");
		produto.setNome("Produto 1");
		produto.setValor(BigDecimal.TEN);
		produtoDao.cadastrar(produto);
		return produto;
	}

	private void excluir(String valor) throws DAOException {
		this.produtoDao.excluir(valor);
	}

	@Test
	public void pesquisar() throws MaisDeUmRegistroException, TableException, DAOException, TipoChaveNaoEncontradaException {
		Produto produto = criarProduto("A1");
		Assert.assertNotNull(produto);
		Produto produtoDB = this.produtoDao.consultar(produto.getCodigo());
		Assert.assertNotNull(produtoDB);
		excluir(produtoDB.getCodigo());
	}

	@Test
	public void salvar() throws TipoChaveNaoEncontradaException, DAOException {
		Produto produto = criarProduto("A2");
		Assert.assertNotNull(produto);
		excluir(produto.getCodigo());
	}

	@Test
	public void excluir() throws DAOException, TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException {
		Produto produto = criarProduto("A3");
		Assert.assertNotNull(produto);
		excluir(produto.getCodigo());
		Produto produtoBD = this.produtoDao.consultar(produto.getCodigo());
		assertNull(produtoBD);
	}

	@Test
	public void alterarCliente() throws TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
		Produto produto = criarProduto("A4");
		produto.setNome("Rodrigo Pires");
		produtoDao.alterar(produto);
		Produto produtoBD = this.produtoDao.consultar(produto.getCodigo());
		assertNotNull(produtoBD);
		assertEquals("Rodrigo Pires", produtoBD.getNome());

		excluir(produto.getCodigo());
		Produto produtoBD1 = this.produtoDao.consultar(produto.getCodigo());
		assertNull(produtoBD1);
	}

	@Test
	public void buscarTodos() throws DAOException, TipoChaveNaoEncontradaException {
		criarProduto("A5");
		criarProduto("A6");
		Collection<Produto> list = produtoDao.buscarTodos();
		assertTrue(list != null);
		assertTrue(list.size() == 2);

		for (Produto prod : list) {
			excluir(prod.getCodigo());
		}

		list = produtoDao.buscarTodos();
		assertTrue(list != null);
		assertTrue(list.size() == 0);
	}

	@Test
	public void testInserirProdutoComCategoria() throws SQLException, DAOException, TipoChaveNaoEncontradaException {
		Produto produto = new Produto();
		produto.setCodigo("ABC123");
		produto.setNome("Chocolate");
		produto.setDescricao("Chocolate ao leite 100g");
		produto.setValor(new BigDecimal("5.50"));
		produto.setCategoria("Alimentos");

		IProdutoDAO dao = new ProdutoDAO();
		Boolean resultado = dao.cadastrar(produto);

		assertTrue(resultado);
	}

	@Test
	public void testInserirProdutoComMarca() throws SQLException, DAOException, TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException {
		Produto produto = new Produto();
		produto.setCodigo("XYZ789");
		produto.setNome("Biscoito");
		produto.setDescricao("Biscoito doce 200g");
		produto.setValor(new BigDecimal("3.50"));
		produto.setCategoria("Alimentos");
		produto.setMarca("Maravilha"); // nova marca

		IProdutoDAO dao = new ProdutoDAO();
		Boolean resultado = dao.cadastrar(produto);

		assertTrue(resultado);

		Produto produtoBD = dao.consultar(produto.getCodigo());
		assertNotNull(produtoBD);
		assertEquals("Maravilha", produtoBD.getMarca()); // valida a marca

		dao.excluir(produto.getCodigo());
	}
}

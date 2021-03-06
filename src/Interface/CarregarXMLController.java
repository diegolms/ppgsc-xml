/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Utils.FuncoesUtils;
import Utils.JTableToPDF;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import entidades.CodeLinks;
import entidades.Codes;
import entidades.Resposta;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Diego
 * @author Lucas
 */
public class CarregarXMLController {
      
    private CarregarXML tela = null;
    private boolean carregouArquivo;
    private static String interesse_name;
    private static String interesse_id;
    private final String[] colunas = {"Concern", "Scattered","Tangled","Crosscutting Concern"};
   
    private String espalhado;
    private String entrelacado;
    private Map<String, List> forneceA, recebeDe;
    
    
    
    /*
    * variaveis auxiliares
    */
    Element tag_codes;
    Element sub_tag_code;
    Element tag_codeLinks;
    Element sub_tag_codeLink;
    Element netView_node;
    Element sub_tag_node;
    Element raiz;
    NodeList lista_sub_tag_code;
    /*
    * objetos de classes externas
    */
    Codes codes;
    CodeLinks cl;
      
    /*
    * Listas dos objetos externos
    */
    ArrayList<Codes> lista_codes;
    ArrayList<CodeLinks> lista_codeLinks;
    ArrayList<String> lista_nomes;
    private int tamanhoListaAux;
    private boolean chamouTodos;
    private List<Integer> listaTamanhoLinha;
    private List<Resposta> resposta;
    
    public CarregarXMLController(CarregarXML tela){
	this.tela = tela;        
    }
    
    public CarregarXML getTela(){ 
	return this.tela;
    }
    
    protected void carregarXML(){
        carregouArquivo = false;
        String tfArquivo = getTela().tfArquivo.getText();
        if(tfArquivo.isEmpty()){
            FuncoesUtils.mostrarMensagemErro(tela, "Error!", "No file selected!");
            return;
        }
        carregarListas(tfArquivo);
       
    } 
    
    private void carregarListas(String arquivo){
        
        try{
            
            lista_codes = new ArrayList<>();            
            lista_nomes = new ArrayList<>();
            
            //fazer o parse do arquivo e criar o documento XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(arquivo));
            //obter o elemento raiz
            raiz = doc.getDocumentElement();

           
            
            //pega o nome do interesse que se deseja saber 
            NodeList netView = raiz.getElementsByTagName("netView");
            netView_node = (Element) netView.item(0);
            Attr aux = netView_node.getAttributeNode("name");
            
            //interesse_name = aux.getNodeValue();
           
            //Pegando a subtag de <netView>, <node>
            NodeList node = netView_node.getElementsByTagName("node");
            
            //localizar o sub-elementos da raiz, a tag <codes>
            NodeList lista_tag_codes = raiz.getElementsByTagName("codes");
            //cast da lista para poder acessar os sub-elementos de <codes>
            // como so existe um elemento <codes> no XML, 
            //o valor do index deve ser 0 para acessar a primeira posição da lista
            tag_codes = (Element) lista_tag_codes.item(0);    
            // localizar o sub-elementos da tag <codes> com o nome <code>
            lista_sub_tag_code = tag_codes.getElementsByTagName("code");             
            //o laço joga todos os elementos na lista
            montarListaComNomes();
            preencherComboBox();
            
            carregouArquivo = true;
            
        }catch(NullPointerException n){
            FuncoesUtils.mostrarMensagemErro(getTela(), "Error", "This is not a valid XML file for this operation!");
        } catch (ParserConfigurationException e) {
            FuncoesUtils.mostrarMensagemErro(getTela(), "Error", "The parser is not configured correctly!");
        } catch (SAXException e) {
            FuncoesUtils.mostrarMensagemErro(getTela(), "Error", "Problem to parse the file!");
        } catch (IOException e) {
            FuncoesUtils.mostrarMensagemErro(getTela(), "Error", "The file can not be read!");
        }
        
    }
    
    protected void carregarTabela(String interesse){
       procurarID(interesse);
       if(!carregouArquivo){
            FuncoesUtils.mostrarMensagem(tela, "Caution!", "XML file not loaded!");
            return;
        } 
         
         getTela().panelTabela.setVisible(true);
         getTela().btImprimir.setEnabled(true);
    }
    
    //metodo para impressão da tabela em pdf
    protected void imprimirTabela(){
        JTableToPDF j = new JTableToPDF(interesse_name, colunas);
        try {
            j.runReport(getTela().tabela, System.getProperty("user.dir"), "TestPdf");
        } catch (Exception e) {
            System.out.println("Error in printing! " + e.getMessage());
        }
    }
    
    public void montarListaComNomes(){
        for (int i=0; i<lista_sub_tag_code.getLength(); i++){     
                sub_tag_code = (Element) lista_sub_tag_code.item(i);
                Attr id = sub_tag_code.getAttributeNode("id");
                Attr name = sub_tag_code.getAttributeNode("name");
                
                //pegando todos os codigos possiveis
                Codes auxCodes = new Codes(id.getNodeValue(), name.getNodeValue());
                lista_codes.add(auxCodes);
                
                //Lista com os nomes de todos os interreses
                lista_nomes.add(name.getValue());  
            } 
      
       
    }
    
    public void preencherComboBox(){
       getTela().cbListarInteresses.removeAllItems();
       getTela().cbListarInteresses.addItem("All");
       for(int i=0; i<lista_nomes.size();i++){
           getTela().cbListarInteresses.addItem(lista_nomes.get(i).toString());
           
       }
       getTela().cbListarInteresses.setSelectedIndex(0);
        
    }
    public void procurarID(String interesse){
        interesse_name = interesse;
        tamanhoListaAux = 0;
        chamouTodos = true;
        listaTamanhoLinha = new LinkedList();
        lista_codeLinks = new ArrayList<>();
        resposta = new LinkedList<Resposta>();
        if(interesse_name.equalsIgnoreCase("all")){
            tamanhoListaAux = lista_nomes.size();   
            for(int i=0; i<tamanhoListaAux; i++){
                interesse_name = lista_nomes.get(i);
                for (int j=0; j<lista_sub_tag_code.getLength(); j++){  
                    sub_tag_code = (Element) lista_sub_tag_code.item(j);
                    Attr name = sub_tag_code.getAttributeNode("name");
                    Attr id = sub_tag_code.getAttributeNode("id");
                    //procura o id do interesse selecionado
                    Codes auxCodes = new Codes(id.getNodeValue(), name.getNodeValue());
                        if (auxCodes.getName().equals(interesse_name)){
                            interesse_id = auxCodes.getId();
                        }        
                }
                getInterreseRelevantes();
                chamarMetodos();
            }
            chamouTodos = false;
            
           
        }
        else{
            for (int i=0; i<lista_sub_tag_code.getLength(); i++){  
                sub_tag_code = (Element) lista_sub_tag_code.item(i);
                Attr name = sub_tag_code.getAttributeNode("name");
                Attr id = sub_tag_code.getAttributeNode("id");
                //procura o id do interesse selecionado
                Codes auxCodes = new Codes(id.getNodeValue(), name.getNodeValue());
                    if (auxCodes.getName().equals(interesse_name)){
                        interesse_id = auxCodes.getId();
                    }        
            }        
        }
        
        getInterreseRelevantes();
        chamarMetodos();
        
        
    }
    
    //Metodo para pegar os interesses que tem ligação com o interesse principal
    private void getInterreseRelevantes(){
        NodeList lista_cl = raiz.getElementsByTagName("codeLinks");
            tag_codeLinks = (Element) lista_cl.item(0);
            NodeList lista_sub_tag_codeLink = tag_codeLinks.getElementsByTagName("codeLink");
            
            for (int i=0; i<lista_sub_tag_codeLink.getLength(); i++){
                cl = new CodeLinks();
                sub_tag_code = (Element) lista_sub_tag_codeLink.item(i);
                Attr rel = sub_tag_code.getAttributeNode("rel");
                Attr fonte = sub_tag_code.getAttributeNode("source");
                Attr alvo = sub_tag_code.getAttributeNode("target");
                if(alvo.getNodeValue().equals(interesse_id) || fonte.getNodeValue().equals(interesse_id)){
                    cl.setRel(rel.getNodeValue());
                    cl.setAlvo(alvo.getNodeValue());
                    cl.setFonte(fonte.getNodeValue());
                    lista_codeLinks.add(cl);
                }
                
            }
    }
    
    private void chamarMetodos(){
            setarTodasAsLigacoes(lista_codes, lista_codeLinks);
            //zerarCampos();
            String resposta = verificarTransversalidade(lista_codeLinks, lista_codes);        
            criarChamarTabela(lista_codeLinks, lista_codes, resposta, chamouTodos);        
            carregouArquivo = true;
    }
    
    
    
    private String recuperarNomeDeCodigo(String id){
        String nome = "";
        for(Codes c: lista_codes){
            if(id.equals(c.getId())){
                nome = c.getName();
                break;
            }
        }
        return nome;
    }
    
    private void setarTodasAsLigacoes(ArrayList<Codes> lista_codes, List<CodeLinks> code_links){
        forneceA = new HashMap<String,List>();
        recebeDe = new HashMap<String,List>();
        for(CodeLinks codigoDaVez: code_links){
            String chaveFornece = recuperarNomeDeCodigo(codigoDaVez.getFonte());
            String chaveRecebe = recuperarNomeDeCodigo(codigoDaVez.getAlvo());
            List listaFornece = new LinkedList();
            List listaRecebe = new LinkedList();
            for(CodeLinks cl: code_links){
                if(codigoDaVez.getFonte().equals(cl.getFonte())){
                    listaFornece.add(recuperarNomeDeCodigo(cl.getAlvo()));                    
            
                }
                if(codigoDaVez.getAlvo().equals(cl.getAlvo())){
                    listaRecebe.add(recuperarNomeDeCodigo(cl.getFonte()));                    
             
                }
            }
            forneceA.put(chaveFornece, listaFornece);
            recebeDe.put(chaveRecebe, listaRecebe);
        }   
        
       
    }
    
    private String verificarTransversalidade(List<CodeLinks> code_links, ArrayList<Codes> lista_codes){
        int cont_alvo = 0;
        int cont_fonte = 0;
        int aux = code_links.size();
        for (int i = 0; i < aux; i++){
            if(code_links.get(i).getAlvo().equals(interesse_id)){
                cont_alvo++; 
            }
            if(code_links.get(i).getFonte().equals(interesse_id)){
                cont_fonte++;
            }
            // condição de parada, caso ja se tenha o resultado
            if ((cont_alvo>=2) && (cont_fonte>=2)){
                break;
            }
            
           
        }
        
        if(cont_alvo >= 2 && cont_fonte >=2){
            return "Yes";
        }else{
            return "No";
        }
    }
    
    /*
     * @lista_codes = lista com os ids e nomes 
     * @code_links = lista com os rels, fontes(sources) e alvos(targets)
     * @resp_transv = é a resposta dizendo se o interesse é transversla ou não
     */
    private void criarChamarTabela(List<CodeLinks> code_links,
            List<Codes> lista_codes, String resp_transv, boolean chamouTodos){
            
            entrelacado = "<html>";
            espalhado = "<html>";
            
            Resposta r = new Resposta();
            for(int i = 0; i < code_links.size() ; i++){
                if(code_links.get(i).getRel().equals("BTP") && 
                        code_links.get(i).getAlvo().equals(interesse_id)){
                        for(int j = 0; j < lista_codes.size();j++){
                            if(code_links.get(i).getFonte().equals(lista_codes.get(j).getId())){
                                entrelacado = entrelacado + "<br>" + lista_codes.get(j).getName();
                            }    
                        }
                        
                }else if(code_links.get(i).getRel().equals("ISIN") &&
                        code_links.get(i).getFonte().equals(interesse_id)){
                        for(int j = 0; j < lista_codes.size();j++){
                            if(code_links.get(i).getAlvo().equals(lista_codes.get(j).getId())){
                                espalhado = espalhado + "<br>" + lista_codes.get(j).getName();
                            }    
                        }
                }
            }
            
            entrelacado += "</html>";
            espalhado+="</html>";
            
            
            String[][] tupla = {
            {interesse_name,espalhado,entrelacado,resp_transv}};
            
            if(chamouTodos){
                r.setInteresse_name(interesse_name);
                r.setEntrelacado(entrelacado);
                r.setEspalhado(espalhado);
                r.setResp(resp_transv);            
                resposta.add(r);
            }
            
            if(tamanhoListaAux == 0){
                chamouTodos = false;
            }
            
            int tamEntrelacado = entrelacado.length();
            int tamEspalhado = espalhado.length();
            int tamanhoFinal = tamEntrelacado > tamEspalhado ? tamEntrelacado : tamEspalhado;
            listaTamanhoLinha.add(tamanhoFinal);
            
            if(!chamouTodos){
                getTela().tabela.setModel(new ModeloTabela(resposta));
                for(int i=0; i<listaTamanhoLinha.size(); i++){
                    getTela().tabela.setRowHeight(i, listaTamanhoLinha.get(i));
                    
                }
                
            }
            
            
                
    }
    
    protected void abrirArquivo(){
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Xml Files", "xml"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = fileChooser.showSaveDialog(null);
        if(i==1){
            
        }else{
            getTela().tfArquivo.setText(fileChooser.getSelectedFile().getAbsolutePath()); 
       }
    }  
    
    protected void zerarCampos(){
        entrelacado = "";
        espalhado = "";
        interesse_id = "";
        interesse_name = "";
        getTela().cbListarInteresses.removeAllItems();
        getTela().cbListarInteresses.addItem("Concerns");
        getTela().panelTabela.setVisible(false);
        getTela().btImprimir.setEnabled(false);
        getTela().tfArquivo.setText("");
    }
    
    
    class ModeloTabela extends AbstractTableModel {

            List<Resposta> resp = new ArrayList<>();

        ModeloTabela(Resposta r) {
            resp.add(r);
        }
        
        ModeloTabela(List<Resposta> resp){
            this.resp = resp;
        }

        public String getColumnName(int c) {
            switch (c) {
                case 0:
                    return "Concern";
                case 1:
                    return "Scattered";
                case 2:
                    return "Tangled";
                case 3:
                    return "Crosscutting Concern";
            }
            return super.getColumnName(c);
        }

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return resp.size();
        }

        public Object getValueAt(int row, int col) {
            Resposta r = resp.get(row);
            
            switch (col) {
                case 0:
                    return resp.get(row).getInteresse_name();
                case 1:
                    return resp.get(row).getEspalhado();
                case 2:
                    return resp.get(row).getEntrelacado();
                case 3:
                    return resp.get(row).getResp();
            }
            return row;
        }
    }
    
    
}

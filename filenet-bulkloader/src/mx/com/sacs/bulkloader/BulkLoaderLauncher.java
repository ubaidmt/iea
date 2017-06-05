package mx.com.sacs.bulkloader;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import mx.com.sacs.bulkloader.constants.BulkLoaderConstants;
import mx.com.sacs.bulkloader.dao.CEConnection;
import mx.com.sacs.bulkloader.util.BulkLoaderUtil;
import mx.com.sacs.bulkloader.util.XMLConfigUtil;
import mx.com.sacs.bulkloader.util.CSVUtil;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.util.UserContext;
import com.itextpdf.text.pdf.PdfReader;

import mx.com.sacs.bulkloader.bean.*;

public class BulkLoaderLauncher {

	private CEConnection ce = null;
	private BulkLoaderBean bulkLoaderBean = null;

	private static int batchNumber = 0;

	private static Logger log = Logger.getLogger(BulkLoaderLauncher.class);

	public static void main(String[] args) {
		BulkLoaderLauncher loader = new BulkLoaderLauncher();
		loader.init();
	}

	private void init() {
		
		// Load Settings
		try {
			bulkLoaderBean = XMLConfigUtil.loadXMLConfig();
		} catch (Exception e) {
			log.error("Ocurrió un error al intentar cargar la configuración del archivo XML.",e);
			System.exit(1);
		}

		BatchConfigBean configBean = bulkLoaderBean.getBatchConfigBeans().get(0);

		if (!configBean.isValidatePDF()) {
			// Attempt CE Connection
			try {
				ce = new CEConnection();
				ce.establishConnection(
						bulkLoaderBean.getCeConnBean().getUser(),
						bulkLoaderBean.getCeConnBean().getPass(),
						bulkLoaderBean.getCeConnBean().getStanza(),
						bulkLoaderBean.getCeConnBean().getUri());
			} catch (Exception e) {
				log.error(
						"Ocurrió un error al intentar abrir la conexión a Content Engine.",	e);
				System.exit(1);
			}
		}

		// Create thread for each BatchConfigBean element
		for (final BatchConfigBean batchConfigBean : bulkLoaderBean.getBatchConfigBeans()){
			Thread t = new Thread(new Runnable() {
				public void run()  {
				
//					log.info("Inicio de thread para carga masiva de documentos.");

					UserContext uc = UserContext.get();
					CsvReader csvReader = null;
					CsvWriter csvWriter = null;
					
					try{
						if (batchConfigBean.isValidatePDF()){
							
							log.info("->->->-> [ INICIA PROCESO DE VALIDACION ] <-<-<-<-");

							csvReader = CSVUtil.getCsvReader(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getFileReference(), batchConfigBean.getDelimeter());
							// Get CsvWriter
							if (!batchConfigBean.getOutputFile().equals(""))
								csvWriter = CSVUtil.getCsvWriter(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getOutputFile(), batchConfigBean.getDelimeter());							
							
							int rowCount = CSVUtil.getRowCount(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getFileReference(), batchConfigBean.getDelimeter(), batchConfigBean.isContainHeader());
							BulkLoaderUtil.setBatchSize(batchConfigBean.getBatchSize());
							BulkLoaderUtil.setBatchCount(rowCount);
							
							validateFiles(batchConfigBean, csvReader, csvWriter);
							log.info("->->->-> [ PROCESO DE VALIDACION FINALIZADO ] <-<-<-<-");
							
						}else{
							log.info("->->->-> [ INICIA EL PROCESO DE CARGA DE ARCHIVOS ] <-<-<-<-");

							csvReader = CSVUtil.getCsvReader(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getFileReference(), batchConfigBean.getDelimeter());
							// Get CsvWriter
							if (!batchConfigBean.getOutputFile().equals(""))
								csvWriter = CSVUtil.getCsvWriter(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getOutputFile(), batchConfigBean.getDelimeter());							
							
							uc.pushSubject(ce.getSubject());
							
							int rowCount = CSVUtil.getRowCount(batchConfigBean.getBaseFolder() + File.separator + batchConfigBean.getFileReference(), batchConfigBean.getDelimeter(), batchConfigBean.isContainHeader());
							BulkLoaderUtil.setOs(ce.fetchOS((String) bulkLoaderBean.getCeConnBean().getOs()));
							BulkLoaderUtil.setBatchSize(batchConfigBean.getBatchSize());
							BulkLoaderUtil.setBatchCount(rowCount);
							
							loadDocuments(batchConfigBean, csvReader, csvWriter);
							log.info("->->->-> [ PROCESO DE CARGA FINALIZADO ] <-<-<-<-");

						}
					}catch (Exception e){
						log.error("Ocurrió un error al realizar la carga de documentos.", e);
					} 
					finally {
						uc.popSubject();
						if (csvReader != null) {
							csvReader.close();
						}
						if (csvWriter != null) {
							csvWriter.close();
						}
					}
				}
			});
			t.start();
		}
	}

	private void loadDocuments(BatchConfigBean batchConfigBean, CsvReader csvReader, CsvWriter csvWriter) throws Exception {
		
		// Load Documents from CSV
		UpdatingBatch ub = BulkLoaderUtil.createBatch(ce.fetchDomain());
		int currentRecord = 1;
		int j = 0;
		File docFile = null;
	
		log.info("Total de documentos a cargar: " + BulkLoaderUtil.getBatchCount());
		
		// Identify Header
		if (batchConfigBean.isContainHeader())
			csvReader.readHeaders();
		
		while (csvReader.readRecord()) 
		{
			try
			{
				log.debug("Procesando registro " + currentRecord + " de " + BulkLoaderUtil.getBatchCount() + " ...");
				
				// Assign date format mask
				BulkLoaderUtil.setSdf(batchConfigBean.getDateFormat());
				
				// Get File
				int docFilePathPosition = BulkLoaderUtil.getDocumentFilePosition(csvReader.getValues(), bulkLoaderBean.getDocClassesBean());
				String relativePath = BulkLoaderUtil.normalizePathSeparators(csvReader.getValues()[docFilePathPosition]);			
				docFile = new File(batchConfigBean.getBaseFolder() + File.separator + relativePath);
				if (!docFile.isFile())
					throw new Exception("El archivo no es válido o no existe en la ruta especificada");
				
				// Create Folder Structure
				
				List<String> subFolderElements = BulkLoaderUtil.getSubFolderElements(relativePath);
				Folder fol = BulkLoaderUtil.createFolderStructureBySegments(BulkLoaderUtil.getOs(), docFile, batchConfigBean.getOsFolder(), subFolderElements, bulkLoaderBean.getFolderClassesBean(), csvReader.getValues());
				
				Document d = null;
				// Validate Document Uniqueness				
				if (batchConfigBean.isValidateDuplicates()) {
//					d = BulkLoaderUtil.getDocument(BulkLoaderUtil.getOs(), fol.get_PathName() + "/" + BulkLoaderUtil.getPropertyDefinition(docFile, csvReader.getValues(), bulkLoaderBean.getDocClassesBean(), "DocumentTitle").getValue());
					d = BulkLoaderUtil.getDocument(BulkLoaderUtil.getOs(), fol.get_PathName() + "/" + docFile.getName());
					if (d != null)
						throw new Exception("El Documento " + fol.get_PathName() + "/" + docFile.getName() + " ya existe en el repositorio de documentos");					
				}
				
				// Create Document
				d = BulkLoaderUtil.createDocument(docFile, BulkLoaderUtil.getOs(), bulkLoaderBean.getDocClassesBean(), csvReader.getValues());
				BulkLoaderUtil.populateBatch(ub, d);
				if (fol != null) {
					ReferentialContainmentRelationship rcr = BulkLoaderUtil.fileDocument(d, docFile.getName(),fol);
					BulkLoaderUtil.populateBatch(ub, rcr);
				}
				log.debug("Documento " + docFile.getName() + " creado");
				j++;
				
				if (csvWriter != null)
					CSVUtil.writeRecord(csvWriter, csvReader.getValues(), BulkLoaderConstants.STATUS_OK, "Documento creado");							
											
			}
			catch (Exception e)
			{
				log.error("Ocurrió un error al intentar procesar el registro " + currentRecord + " correspondiente al Batch " + batchNumber, e);
				if (csvWriter != null)
					CSVUtil.writeRecord(csvWriter, csvReader.getValues(), BulkLoaderConstants.STATUS_ERR, e.getMessage());				
			} 
			finally 
			{
				
				// Update Batch
				if (ub.hasPendingExecute() && (j >= BulkLoaderUtil.getBatchSize() || currentRecord >= BulkLoaderUtil.getBatchCount()))
				{
					ub.updateBatch();
					j = 0;
					++batchNumber;
					log.debug("Batch " + batchNumber + " completado");
				}				
				
				currentRecord++;
			}

		}
		
	}
		
	private void validateFiles(BatchConfigBean batchConfigBean, CsvReader csvReader, CsvWriter csvWriter) throws Exception {
		
		File pdfFile;
		PdfReader pdf;
		FileInputStream inputStream = null;
		
		// Load Documents from CSV
		int j = 0;
		int currentRecord = 1;
		
		log.info("Total de documentos a por validar: "
				+ BulkLoaderUtil.getBatchCount());

		// Identify Header
		if (batchConfigBean.isContainHeader())
			csvReader.readHeaders();

		while (csvReader.readRecord()) {
			
			try {
				
				log.debug("Procesando registro (" + currentRecord + ") de (" + BulkLoaderUtil.getBatchCount() + ")");

				// Assign date format mask
				BulkLoaderUtil.setSdf(batchConfigBean.getDateFormat());

				// Get File
				int filePathPosition = BulkLoaderUtil.getDocumentFilePosition(csvReader.getValues(),bulkLoaderBean.getDocClassesBean());
				String relativePath = BulkLoaderUtil.normalizePathSeparators(csvReader.getValues()[filePathPosition]);
				pdfFile = new File(batchConfigBean.getBaseFolder()+ File.separator + relativePath);
				
				if (!pdfFile.isFile())
					throw new Exception("El archivo [" + pdfFile.getName() + "] no existe en la ruta especificada.");
				
				int numberPages = Integer.parseInt(csvReader.getValues()[BulkLoaderUtil.getNumberPages(csvReader.getValues(),bulkLoaderBean.getDocClassesBean())]);
				
				if (pdfFile.getName().indexOf(".pdf") == -1)
					throw new Exception("El archivo: " + pdfFile.getName() + " no es un documento PDF.");
				
				try{
					inputStream = new FileInputStream(pdfFile);
					pdf = new PdfReader(inputStream);
				}catch (Exception e) {
					log.error(e.getMessage(), e);
					throw new Exception("Imposible acceder al archivo ( " + pdfFile.getName() + " ), archivo dañado o tipo no soportado.");
				}
				
				if(pdf.getNumberOfPages() != numberPages)
					throw new Exception(" El numero de páginas del archivo (" + pdf.getNumberOfPages() +
							") no coincide con el del reporte (" + numberPages +")");

				if (csvWriter != null)
					CSVUtil.writeRecord(csvWriter, csvReader.getValues(),
							BulkLoaderConstants.STATUS_OK, "Documento validado con éxito.");
				j++;


			} catch (Exception e) {
				log.error("Ocurrió un error al intentar procesar el registro "
						+ currentRecord + " correspondiente al Batch "
						+ batchNumber, e);
				if (csvWriter != null)
					CSVUtil.writeRecord(csvWriter, csvReader.getValues(),
							BulkLoaderConstants.STATUS_ERR, e.getMessage());
			} finally {
				
				// Update Batch
				if (j >= BulkLoaderUtil.getBatchSize() || currentRecord >= BulkLoaderUtil.getBatchCount())
				{
					j = 0;
					++batchNumber;
					log.debug("Batch " + batchNumber + " completado");
				}
				currentRecord++;
			}

		}

	}

}

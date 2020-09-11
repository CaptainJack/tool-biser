package ru.capjack.tool.io.biser.generator.kotlin

import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.analyzer.ResolverForSingleModuleProject
import org.jetbrains.kotlin.analyzer.common.CommonAnalysisParameters
import org.jetbrains.kotlin.analyzer.common.CommonPlatformAnalyzerServices
import org.jetbrains.kotlin.analyzer.common.CommonResolverForModuleFactory
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.tryGetService
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.CompilerEnvironment
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzer
import org.jetbrains.kotlin.resolve.PlatformDependentAnalyzerServices
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.slf4j.LoggerFactory
import java.nio.file.Path

class KotlinSource(dir: Path) {
	val classDescriptors: Collection<ClassDescriptor>
	
	private val logger = LoggerFactory.getLogger(this::class.java)
	
	init {
		
		val messageCollector = object : MessageCollector {
			private var hasErrors = false
			
			override fun clear() {
				hasErrors = false
			}
			
			override fun hasErrors() = hasErrors
			
			override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
				val text = if (location == null) message else "${location.path}: (${location.line}, ${location.column}) $message"
				when {
					severity.isError                         -> logger.error(text)
					severity.isWarning                       -> logger.warn(text)
					severity == CompilerMessageSeverity.INFO -> logger.info(text)
					else                                     -> logger.debug(text)
				}
			}
		}
		
		val moduleInfo = object : ModuleInfo {
			override val name: Name = Name.special("<main>")
			override val analyzerServices: PlatformDependentAnalyzerServices = CommonPlatformAnalyzerServices
			override val platform: TargetPlatform = CommonPlatforms.defaultCommonPlatform
			override fun dependencies(): List<ModuleInfo> = listOf(this)
		}
		
		
		val configuration = CompilerConfiguration()
		configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
		configuration.addKotlinSourceRoot(dir.toAbsolutePath().toString(), true)
		
		val rootDisposable = Disposer.newDisposable()
		
		try {
			val environment = KotlinCoreEnvironment.createForProduction(rootDisposable, configuration, EnvironmentConfigFiles.METADATA_CONFIG_FILES)
			
			val ktFiles = environment.getSourceFiles()
			val project = ktFiles.first().project
			
			val resolverForModuleFactory = CommonResolverForModuleFactory(
				CommonAnalysisParameters { content -> environment.createPackagePartProvider(content.moduleContentScope) },
				CompilerEnvironment,
				CommonPlatforms.defaultCommonPlatform,
				shouldCheckExpectActual = false
			)
			
			val resolver = ResolverForSingleModuleProject(
				"CSI",
				ProjectContext(project, "CSI"),
				moduleInfo,
				resolverForModuleFactory,
				GlobalSearchScope.allScope(project),
				syntheticFiles = ktFiles
			)
			
			val container = resolver.resolverForModule(moduleInfo).componentProvider
			
			val lazyTopDownAnalyzer = container.tryGetService(LazyTopDownAnalyzer::class.java) as LazyTopDownAnalyzer
			val context = lazyTopDownAnalyzer.analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, ktFiles)
			
			classDescriptors = context.declaredClasses.values
		}
		finally {
			rootDisposable.dispose()
		}
	}
}
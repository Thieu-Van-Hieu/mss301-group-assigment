import {Header} from './components/Header';
import {GroupSection} from './components/GroupSection';
import {GeneralSection} from './components/sections/GeneralSection';
import {FormSection} from './components/sections/FormSection';
import {DataSection} from './components/sections/DataSection';
import {NavigationSection} from './components/sections/NavigationSection';
import {FeedbackSection} from './components/sections/FeedbackSection';
import {LayoutSection} from './components/sections/LayoutSection';
import {DocSection} from './components/sections/DocSection';

function App() {
	return (
		<div className="text-slate-800 antialiased pb-24">
			<Header/>

			<main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-10 space-y-16">
				{/* Intro Hero */}
				<div
					className="bg-white rounded-3xl p-8 border-2 border-slate-200 shadow-[0_6px_0_#cbd5e1] relative overflow-hidden">
					<div className="max-w-3xl space-y-3">
            <span
				className="px-3 py-1 bg-orange-100 text-orange-600 rounded-xl text-xs font-black uppercase tracking-wider">
              Design System Template
            </span>
						<h2 className="text-3xl font-black text-slate-800">Thư Viện 3D Tactile UI Components</h2>
						<p className="text-slate-600 font-semibold leading-relaxed text-sm">
							Tất cả thành phần UI dưới đây đều chuẩn hóa theo style React `AppButton` & `AppInput` (Sử
							dụng viền nổi 2px, đổ bóng 3D 4px, góc bo `rounded-2xl`, hiệu ứng chìm
							`active:translate-y-1`).
						</p>
					</div>
				</div>

				<GroupSection id="g1" number={1} title="Group 1: General & Typography">
					<GeneralSection/>
				</GroupSection>

				<GroupSection id="g2" number={2} title="Group 2: Form & Inputs">
					<FormSection/>
				</GroupSection>

				<GroupSection id="g3" number={3} title="Group 3: Data Display">
					<DataSection/>
				</GroupSection>

				<GroupSection id="g4" number={4} title="Group 4: Navigation">
					<NavigationSection/>
				</GroupSection>

				<GroupSection id="g5" number={5} title="Group 5: Feedback & Overlays">
					<FeedbackSection/>
				</GroupSection>

				<GroupSection id="g6" number={6} title="Group 6: Layout & Utilities">
					<LayoutSection/>
				</GroupSection>

				<GroupSection id="g7" number={7} title="Group 7: Doc Kit & Code Viewer">
					<DocSection/>
				</GroupSection>
			</main>
		</div>
	);
}

export default App;